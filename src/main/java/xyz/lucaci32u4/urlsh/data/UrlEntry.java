package xyz.lucaci32u4.urlsh.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class UrlEntry {

    @Id
    @Getter @Setter
    private String reference;

    @Lob
    @Getter @Setter
    private String url;

    @Getter @Setter
    private String creatorIp;

    @Getter @Setter
    private Instant creatorTs;

    @Getter @Setter
    private boolean inpageRedirect;

    // todo insert stats here lazy loaded

}
