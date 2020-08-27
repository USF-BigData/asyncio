package edu.usfca.cs.asyncio;

import java.util.ArrayList;
import java.util.List;

public class ConnectionStats {
    public long bytes = 0;
    public List<String> messages = new ArrayList<>();

    public String toString() {
        return "Received " + bytes + " bytes, "
                + this.messages.size() + " messages.";
    }
}
