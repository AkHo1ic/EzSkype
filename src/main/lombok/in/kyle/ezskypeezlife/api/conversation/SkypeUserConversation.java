package in.kyle.ezskypeezlife.api.conversation;

import in.kyle.ezskypeezlife.api.user.SkypeUser;

/**
 * Created by Kyle on 11/28/2015.
 * <p>
 * Used for 1 on 1 conversations
 */
public interface SkypeUserConversation extends SkypeConversation {
    
    /**
     * Gets the other user in this conversaiton
     *
     * @return - The other user
     */
    SkypeUser getParticipant();
    
}
