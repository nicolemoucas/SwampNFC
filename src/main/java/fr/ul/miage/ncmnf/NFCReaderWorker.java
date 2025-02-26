package fr.ul.miage.ncmnf;

import javax.smartcardio.*;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NFCReaderWorker extends SwingWorker<Void, String> implements PropertyChangeListener {
    private final JTextArea textArea;
    private final JFrame frame;

    public NFCReaderWorker(JTextArea textArea, JFrame frame) {
        this.textArea = textArea;
        this.frame = frame;
    }

    @Override
    protected Void doInBackground() {
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals;
        try {
            terminals = factory.terminals().list();
        } catch (CardException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la recherche des terminaux NFC.", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            });
            return null;
        }

        if (terminals.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frame, "Aucun terminal NFC trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            });
            return null;
        }
        CardTerminal terminal = terminals.getFirst();
        while (true) {
            try {
                terminal.waitForCardPresent(0);
                Card card = terminal.connect("T=1");
                CardChannel channel = card.getBasicChannel();
                String decodedMessage = decodeNDEFMessage(readNDEFMessage(channel));
                publish(decodedMessage);
                terminal.waitForCardAbsent(0);
                publish("Aucune carte n'a été détectée.");
            } catch (CardException e) {
                publish("Erreur: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Le terminal NFC a été débranché ou une erreur s'est produite.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                });
                break;
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            textArea.setText(message);
        }
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            textArea.append("Erreur: " + e.getMessage() + "\n");
        }
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        textArea.setText(evt.getNewValue().toString());
    }


    private static byte[] readNDEFMessage(CardChannel channel) throws CardException {
        int startPage = 4; // Start reading from page 4
        int endPage = 135; // End reading at page 135
        byte[] data = new byte[(endPage - startPage + 1) * 4];

        for (int page = startPage; page <= endPage; page++) {
            byte[] commandAPDU = {(byte) 0xFF, (byte) 0xB0, 0x00, (byte) page, 0x04}; // Command to read 4 bytes from the specified page
            ResponseAPDU response = channel.transmit(new CommandAPDU(commandAPDU));
            byte[] pageData = response.getData();
            if (pageData.length == 0) {
                return data;
            }
            System.arraycopy(pageData, 0, data, (page - startPage) * 4, pageData.length);
        }

        return data;
    }

    private static String decodeNDEFMessage(byte[] data) {
        // Vérifier si les données commencent par le TLV NDEF (0x03)
        if (data.length < 2 || data[0] != 0x03) {
            return "Données NDEF non valides.";
        }

        // Lire la longueur du message NDEF
        int ndefLength = data[1] & 0xFF;

        // Vérifier si la longueur du NDEF est valide
        if (ndefLength > data.length - 2) {
            return "Longueur NDEF non valide.";
        }

        byte[] ndefHeader = new byte[5];
        System.arraycopy(data, 2, ndefHeader, 0, ndefHeader.length);

        // NDEF Header for URI Record
        if (!(ndefHeader[0] == (byte) 0xD1 // ndefMessage[0] = (byte) 0xD1; // NDEF Header (MB/ME/SR/IL/TNF)
                && ndefHeader[1] == (byte) 0x01 // ndefMessage[1] = (byte) 0x01; // Type Length
                && ndefHeader[3] == (byte) 0x55 // ndefMessage[3] = (byte) 0x55; // Type (URI)
                && ndefHeader[4] == (byte) 0x00)) { // ndefMessage[4] = (byte) 0x00; // URI Identifier Code (0x00 = No prefix)
            return "Le message NDEF n'est pas de type URI";
        }

        // ndefMessage[2] = (byte) (urlBytes.length + 1); // Payload Length
        int uriLength = (ndefHeader[2] & 0xFF) - 1;

        byte[] uriMessage = new byte[uriLength];
        // Retire le TLV (2 bytes) et le Header (5 bytes)
        System.arraycopy(data, ndefHeader.length + 2, uriMessage, 0, uriLength);

        return new String(uriMessage, StandardCharsets.UTF_8);
    }
}
