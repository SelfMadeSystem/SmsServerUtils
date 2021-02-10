package uwu.smsgamer.smsserverutils.listener;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.*;
import io.github.retrooper.packetevents.packettype.PacketType;
import uwu.smsgamer.smsserverutils.managers.ChatFilterManager;

public class PacketProcessor extends PacketListenerDynamic {
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        System.out.println(event.getPacketName());
    }
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
//        System.out.println(event.getPacketName() + ":" + event.getPacketId() + ":" + PacketType.Play.Server.CHAT);
        if (event.getPacketId() == PacketType.Play.Server.CHAT) {
            ChatFilterManager.getInstance().packetSendEvent(event);
        }
    }
}
