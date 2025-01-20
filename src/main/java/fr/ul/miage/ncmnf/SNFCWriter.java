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

            // TODO: Check if no readers are available
            System.out.println("No NFC readers found.");

            // TODO: Select the first terminal of type CardTerminal (terminal)
            System.out.println("Using reader: " + terminal.getName());

            // TODO: Wait for a card to be *present*
            // Hint: check the suggested methods from CardTerminal
            // Hint 2: Select no timeout
            System.out.println("Waiting for NFC card...");

            // Connect to the card
            Card card = terminal.connect("*");
            // TODO: Get the card's basic channel
            CardChannel channel; // TODO Complete this line
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
            // TODO: Create the NDEF message (ndefMessage)
            // Hint: The message is a byte array

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
                        // TODO: Pad with zeros
                        // Hint: Use hexadecimal format
                        // Hint 2: Cast the hex to byte
                        command[5 + j];
                    }
                }

                // Transmit the command to write to the page
                // TODO: Transmit the command through the channel
                // Hint: The response is a ResponseAPDU (response)
                // Hint 2: Create a CommandAPDU with the previous command
                // Complete here
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
