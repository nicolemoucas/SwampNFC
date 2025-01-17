package fr.ul.miage.ncmnf;

import javax.smartcardio.*;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class SNFCReader {

    public static void main(String[] args) {
        try {
            // Obtenir le terminal NFC
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("Aucun terminal NFC trouvé.");
                return;
            }

            // Utiliser le premier terminal disponible
            CardTerminal terminal = terminals.getFirst();

            // Attendre qu'une carte soit insérée
            System.out.println("Veuillez approcher la puce NFC du lecteur...");
            terminal.waitForCardPresent(0);

            // Connexion à la carte
            Card card = terminal.connect("T=1");
            CardChannel channel = card.getBasicChannel();

            // Lire les données de la puce NFC
            byte[] data = readNDEFMessage(channel);

            // Décoder les données NDEF
            String decodedData = decodeNDEFMessage(data);
            System.out.println("Données lues de la puce NFC: " + decodedData);

            // Déconnexion de la carte
            card.disconnect(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readNDEFMessage(CardChannel channel) throws CardException {
        int startPage = 4; // Start reading from page 4
        int endPage = 17; // End reading at page 17 (adjust as needed)
        byte[] data = new byte[(endPage - startPage + 1) * 4];

        for (int page = startPage; page <= endPage; page++) {
            byte[] commandAPDU = {(byte) 0xFF, (byte) 0xB0, 0x00, (byte) page, 0x04}; // Command to read 4 bytes from the specified page
            ResponseAPDU response = channel.transmit(new CommandAPDU(commandAPDU));
            byte[] pageData = response.getData();
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

        // Vérifier si la longueur est valide
        if (ndefLength > data.length - 2) {
            return "Longueur NDEF non valide.";
        }

        byte[] ndefMessage = new byte[ndefLength];
        System.arraycopy(data, 2, ndefMessage, 0, ndefLength);

        if (ndefMessage.length > 4 && ndefMessage[0] == (byte) 0xD1 && ndefMessage[3] == (byte) 0x55) {
            byte[] urlBytes = new byte[ndefMessage.length-7];
            System.arraycopy(ndefMessage, 5, urlBytes, 0, ndefMessage.length -7 );
            return new String(urlBytes, StandardCharsets.UTF_8);

        }

        return "Format NDEF non reconnu.";
    }
}

