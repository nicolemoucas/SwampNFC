package fr.ul.miage.ncmnf;

import javax.smartcardio.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SNFCReaderBadger {

    private final static Logger LOG = Logger.getLogger(SNFCReaderBadger.class.getName());


    public static void main(String[] args) {
        try {
            HashMap<String, Integer> badgeageHistory = new HashMap<>();

            FileHandler fileHandler = new FileHandler("NFC-Reader.log");
            fileHandler.setFormatter(new SimpleFormatter());
            LOG.addHandler(fileHandler);


            CardTerminal terminal = getCardTerminal();
            if (terminal == null) return;

            while (true) {

                try {
                    System.out.println("Waiting for NFC card...");
                    terminal.waitForCardPresent(0);

                    Thread.sleep(100);

                    String id = readCard(terminal);

                    badgeage(id, badgeageHistory);

                    terminal.waitForCardAbsent(0);
                } catch (CardException e) {
                    e.printStackTrace();
                }
                // Wait for a card to be present

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void badgeage(String id, HashMap<String, Integer> badgeageHistory) {
        Integer nbBadgeage = badgeageHistory.get(id);
        if (nbBadgeage == null) {
            nbBadgeage = 0;
        }
        nbBadgeage++;
        badgeageHistory.put(id, nbBadgeage);

        StringBuilder stringBuilder = new StringBuilder();


        if (nbBadgeage % 2 != 0) {
            stringBuilder.append("Entrée ");
        } else {
            stringBuilder.append("Sortie ");
        }
        stringBuilder.append("de ").append(id);
        stringBuilder.append(" | ").append(nbBadgeage);
        if (nbBadgeage == 1) {
            stringBuilder.append("er ");
        } else {
            stringBuilder.append("eme ");
        }
        stringBuilder.append("badgeage de la journée");

        LOG.info(stringBuilder.toString());


        if (nbBadgeage > 9) {
            LOG.info("Ca fait beaucoup là non ? Tu n'as pas du travail " + id + " ???");
        }
    }

    private static String readCard(CardTerminal terminal) throws CardException {
        // Connect to the card
        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();
        System.out.println("Card detected!");

        // Lire les données de la puce NFC avec les méthodes fournies
        byte[] data = readNDEFMessage(channel);

        // Décoder les données NDEF avec les méthodes fournies
        String decodedData = decodeNDEFMessage(data);
        System.out.println("Données lues de la puce NFC: " + decodedData);


        // Déconnexion de la carte
        card.disconnect(true);

        return decodedData;
    }

    private static CardTerminal getCardTerminal() throws CardException {
        // Obtenir le terminal NFC en s'inspirant de la classe précédente
        // Detect readers
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();

        if (terminals.isEmpty()) {
            System.out.println("No NFC readers found.");
            return null;
        }

        // Select the first terminal
        CardTerminal terminal = terminals.getFirst();
        System.out.println("Using reader: " + terminal.getName());
        return terminal;
    }

    private static byte[] readNDEFMessage(CardChannel channel) throws CardException {
        int startPage = 4; // Start reading from page 4
        int endPage = 135; // End reading at page 17 (adjust as needed)
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
            return "Le message NDEF est pas de type URI";
        }

        // ndefMessage[2] = (byte) (urlBytes.length + 1); // Payload Length
        int uriLength = (ndefHeader[2] & 0xFF) - 1;

        byte[] uriMessage = new byte[uriLength];
        // Retire le TLV (2 bytes) et le Header (5 bytes)
        System.arraycopy(data, ndefHeader.length + 2, uriMessage, 0, uriLength);

        return new String(uriMessage, StandardCharsets.UTF_8);
    }
}

