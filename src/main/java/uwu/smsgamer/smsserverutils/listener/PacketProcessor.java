package uwu.smsgamer.smsserverutils.listener;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.*;

public class PacketProcessor extends PacketListenerDynamic {
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        System.out.println(event.getPacketName());
    }
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        System.out.println(event.getPacketName());
    }
}
