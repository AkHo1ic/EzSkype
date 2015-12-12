package in.kyle.ezskypeezlife.internal.packet.user;

import in.kyle.ezskypeezlife.EzSkype;
import in.kyle.ezskypeezlife.internal.packet.ContentType;
import in.kyle.ezskypeezlife.internal.packet.HTTPRequest;
import in.kyle.ezskypeezlife.internal.packet.SkypePacket;
import in.kyle.ezskypeezlife.internal.packet.WebConnectionBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kyle on 12/12/2015.
 */
public class SkypeSetProfilePicturePacket extends SkypePacket {
    
    private final InputStream image;
    
    public SkypeSetProfilePicturePacket(EzSkype ezSkype, InputStream image) {
        super(String.format("https://api.skype.com/users/%s/profile/avatar", ezSkype.getLocalUser().getUsername()), HTTPRequest.PUT, 
                ezSkype, true);
        this.image = image;
    }
    
    @Override
    protected Object run(WebConnectionBuilder webConnectionBuilder) throws IOException {
        webConnectionBuilder.setContentType(ContentType.IMAGE);
        webConnectionBuilder.setWriteStream(image);
        return webConnectionBuilder.getAsJsonObject();
    }
}
