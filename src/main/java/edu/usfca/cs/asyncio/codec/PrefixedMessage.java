package edu.usfca.cs.asyncio.codec;

/**
 * Encapsulates a simple message with a length prefix.
 *
 * This basically only exists so that we can 
 **/
public class PrefixedMessage {

    private byte[] payload;

    /**
     * Constructs a prefixed message from an array of bytes.
     *
     * @param payload message payload in the form of a byte array.
     */
    public PrefixedMessage(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Retrieves the payload for this prefixed message.
     *
     * @return the PrefixedMessage payload
     */
    public byte[] payload() {
        return payload;
    }

}
