package xyz.lucaci32u4.urlsh.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Getter @Setter
    private boolean logVisits;

    @Getter
    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Visitor> visits = new ArrayList<>(0);

}
