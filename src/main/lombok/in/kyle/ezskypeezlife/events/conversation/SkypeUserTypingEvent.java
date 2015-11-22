package in.kyle.ezskypeezlife.events.conversation;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import in.kyle.ezskypeezlife.events.SkypeConversationEvent;
import in.kyle.ezskypeezlife.events.SkypeEvent;
import in.kyle.ezskypeezlife.events.SkypeUserEvent;
import lombok.Data;

/**
 * Created by Kyle on 10/7/2015.
 */
@Data
public class SkypeUserTypingEvent implements SkypeEvent, SkypeUserEvent, SkypeConversationEvent {
    
    private final SkypeUser user;
    private final SkypeConversation conversation;
    
}
