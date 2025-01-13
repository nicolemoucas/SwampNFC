package fr.ul.miage.ncmnf;

import javax.smartcardio.*;
import java.util.List;

public class SNFCWriter {
    public static void main(String[] args) {
        String SHREKSOPHONE = "https://youtu.be/vXYVfk7agqU?si=YRzvv9M05xF8nsnf";

        try {
            // Detect readers
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                System.out.println("No NFC readers found.");
                return;
            }

            // Select the first terminal
            CardTerminal terminal = terminals.getFirst();
            System.out.println("Using reader: " + terminal.getName());

            // Wait for a card to be present
            System.out.println("Waiting for NFC card...");
            terminal.waitForCardPresent(0);

            // Connect to the card
            Card card = terminal.connect("*");
            CardChannel channel = card.getBasicChannel();
            System.out.println("Card detected!");

            // Write to the NFC tag
            writeUrlToUltralight(channel, SHREKSOPHONE);

            // Disconnect
            card.disconnect(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeUrlToUltralight(CardChannel channel, String url) {
        try {
            // Step 1: Convert the URL into NDEF format for Ultralight
            byte[] ndefMessage = createNDEFMessage(url);

            // Step 2: Write the NDEF message to consecutive pages
            int page = 4; // Start writing at Page 4
            for (int i = 0; i < ndefMessage.length; i += 4) {
                byte[] command = new byte[9]; // Command to write 4 bytes to a page
                command[0] = (byte) 0xFF; // Class
                command[1] = (byte) 0xD6; // INS (Write Binary)
                command[2] = 0x00;        // P1
                command[3] = (byte) page; // P2 (Page Address)
                command[4] = 0x04;        // Lc (Number of bytes to write)

                // Fill the command buffer with 4 bytes of data, padded with 0x00 if less than 4 bytes remain
                for (int j = 0; j < 4; j++) {
                    if (i + j < ndefMessage.length) {
                        command[5 + j] = ndefMessage[i + j];
                    } else {
                        command[5 + j] = (byte) 0x00; // Pad with zeros
                    }
                }

                // Transmit the command to write to the page
                ResponseAPDU response = channel.transmit(new CommandAPDU(command));
                if (response.getSW1() == 0x90 && response.getSW2() == 0x00) {
                    System.out.println("Page " + page + " written successfully.");
                } else {
                    System.out.println("Failed to write to page " + page + ": " +
                            Integer.toHexString(response.getSW1()) + " " +
                            Integer.toHexString(response.getSW2()));
                    return;
                }

                page++;
            }

            System.out.println("URL successfully written to the NFC tag!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] createNDEFMessage(String url) {
        byte[] urlBytes = url.getBytes();
        byte[] ndefMessage = new byte[urlBytes.length + 7];

        // NDEF Header for URI Record
        ndefMessage[0] = (byte) 0x03; // NDEF Message TLV
        ndefMessage[1] = (byte) (urlBytes.length + 5); // Length of the NDEF message
        ndefMessage[2] = (byte) 0xD1; // NDEF Header (MB/ME/SR/IL/TNF)
        ndefMessage[3] = (byte) 0x01; // Type Length
        ndefMessage[4] = (byte) (urlBytes.length + 1); // Payload Length
        ndefMessage[5] = (byte) 0x55; // Type (URI)
        ndefMessage[6] = (byte) 0x00; // URI Identifier Code (0x00 = No prefix)
        System.arraycopy(urlBytes, 0, ndefMessage, 7, urlBytes.length);

        return ndefMessage;
    }
}
