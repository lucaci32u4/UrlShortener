package xyz.lucaci32u4.urlsh.data.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.lucaci32u4.urlsh.Utils;
import xyz.lucaci32u4.urlsh.data.UrlRepository;

import javax.transaction.Transactional;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class StatsManager {

    @Autowired
    private StatsRepository statsRepository;

    @Autowired
    private UrlRepository urlRepository;

    private static final ReentrantLock lock = new ReentrantLock();

    @Transactional
    public String allocateNewReference() {
        lock.lock();
        var optStats = statsRepository.findById(0L);
        if (optStats.isEmpty()) {
            var stats = new Stats();
            stats.setOccupiedSequenceReferences(1L);
            statsRepository.save(stats);
            lock.unlock();
            return Utils.numberToReference(stats.getOccupiedSequenceReferences());
        } else {
            long cand = optStats.get().getOccupiedSequenceReferences() + 1;
            while (urlRepository.findById(Utils.numberToReference(cand)).isPresent()) {
                cand++;
            }
            optStats.get().setOccupiedSequenceReferences(cand);
            statsRepository.save(optStats.get());
            lock.unlock();
            return Utils.numberToReference(cand);
        }
    }


}
