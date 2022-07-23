package xyz.lucaci32u4.urlsh.data.stats;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stats {

    @Getter
    @Id
    private final Long internalIdZero = 0L;

    @Getter @Setter
    private Long occupiedSequenceReferences = 0L;



}
