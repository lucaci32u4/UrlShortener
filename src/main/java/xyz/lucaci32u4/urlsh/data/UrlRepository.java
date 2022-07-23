package xyz.lucaci32u4.urlsh.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UrlRepository extends CrudRepository<UrlEntry, String> {

}
