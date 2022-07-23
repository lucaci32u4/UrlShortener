package xyz.lucaci32u4.urlsh.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface VisitorRepository extends CrudRepository<Visitor, Long> {
}
