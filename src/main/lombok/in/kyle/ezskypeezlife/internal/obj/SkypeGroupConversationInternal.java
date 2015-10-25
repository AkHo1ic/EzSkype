package in.kyle.ezskypeezlife.internal.obj;

import in.kyle.ezskypeezlife.EzSkype;
import in.kyle.ezskypeezlife.api.SkypeConversationPermission;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import in.kyle.ezskypeezlife.internal.packet.conversation.SkypeConversationGetJoinUrl;
import in.kyle.ezskypeezlife.internal.packet.conversation.SkypeConversationKickPacket;
import in.kyle.ezskypeezlife.internal.packet.conversation.SkypeConversationRolePacket;
import in.kyle.ezskypeezlife.internal.packet.conversation.SkypeConversationTopicPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by Kyle on 10/7/2015.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SkypeGroupConversationInternal extends SkypeConversationInternal {
    
    private final List<SkypeConversationPermission> permissions;
    private final boolean joinEnabled;
    private final SkypeUserInternal creator;
    private final List<SkypeUserInternal> admins;
    
    // @formatter:off
    public SkypeGroupConversationInternal(
            EzSkype ezSkype,
            String longId,
            String topic,
            boolean historyEnabled,
            List<SkypeConversationPermission> permissions,
            boolean joinEnabled,
            SkypeUserInternal creator, 
            String url, 
            List<SkypeUserInternal> users,
            List<SkypeUserInternal> admins) {
        super(ezSkype, longId, topic, historyEnabled, joinEnabled, url, SkypeConversationType.GROUP, users);
        this.permissions = permissions;
        this.joinEnabled = joinEnabled;
        this.creator = creator;
        this.users = users;
        this.admins = admins;
    }
    // @formatter:on
    
    public void setRole(SkypeUserInternal skypeUserInternal, SkypeUserRole skypeUserRole) {
        new SkypeConversationRolePacket(ezSkype, longId, skypeUserInternal.getUsername(), skypeUserRole).executeAsync();
    }
    
    public SkypeUserRole getSkypeRole(SkypeUserInternal skypeUserInternal) {
        return admins.contains(skypeUserInternal) ? SkypeUserRole.ADMIN : SkypeUserRole.USER;
    }
    
    public void kick(SkypeUserInternal skypeUserInternal) {
        new SkypeConversationKickPacket(ezSkype, longId, skypeUserInternal.getUsername()).executeAsync();
    }
    
    @Override
    public boolean kick(SkypeUser skypeUser) {
        new SkypeConversationKickPacket(ezSkype, longId, skypeUser.getUsername()).executeAsync();
        return true;
    }
    
    @Override
    public boolean isAdmin(SkypeUser skypeUser) {
        return admins.contains(skypeUser);
    }
    
    @Override
    public String getJoinUrl() throws Exception {
        return (String) new SkypeConversationGetJoinUrl(ezSkype, longId).executeSync();
    }
    
    @Override
    public void setTopic(String topic) {
        new SkypeConversationTopicPacket(ezSkype, longId, topic).executeAsync();
        super.setTopic(topic);
    }
}
