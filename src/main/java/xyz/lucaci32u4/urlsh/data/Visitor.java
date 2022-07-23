package xyz.lucaci32u4.urlsh.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Visitor {

    @Id
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @Getter @Setter
    private String address;

    @Getter @Setter
    private Instant timestamp;


    @Getter
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "url")
    @JsonIgnore
    private UrlEntry url;

    public void setUrl(UrlEntry url) {
        if (this.url != null) this.url.getVisits().remove(this);
        this.url = url;
        if (this.url != null) this.url.getVisits().add(this);
    }
}
