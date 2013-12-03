/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import net.developithecus.parser.definitions.AbstractDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ExpressionCache {
    private static final Logger logger = Logger.getLogger(ExpressionCache.class.getName());
    private Map<Integer, Map<Integer, CacheItem>> cache = new HashMap<Integer, Map<Integer, CacheItem>>();

    public void clear() {
        cache.clear();
    }

    public CacheItem getCacheItem(Position pos, AbstractDefinition def) {
        Integer index = pos.getIndex();
        Map<Integer, CacheItem> set = cache.get(index);
        if (set == null) {
            return null;
        }
        Integer identity = System.identityHashCode(def);
        return set.get(identity);
    }

    private void putItem(Position pos, AbstractDefinition def, CacheItem item) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "putItem({0}, {1}, {2}", new Object[]{pos, def, item});
        }
        Integer index = pos.getIndex();
        Map<Integer, CacheItem> set = cache.get(index);
        if (set == null) {
            set = new HashMap<Integer, CacheItem>();
            cache.put(index, set);
        }
        Integer identity = System.identityHashCode(def);
        if (set.put(identity, item) != null) {
            throw new IllegalStateException("added twice " + pos);
        }
    }

    public void addRollback(Position pos, AbstractDefinition def, String msg) {
        putItem(pos, def, createRollbackItem(msg));
    }

    public void addCommit(Position pos, AbstractDefinition def, Group result, Position resultPos) {
        putItem(pos, def, createCommitItem(result, resultPos));
    }

    private CacheItem createRollbackItem(String msg) {
        return new RollbackItem(msg);
    }

    private CacheItem createCommitItem(Group result, Position resultPos) {
        return new CommitItem(result, resultPos);
    }

    public static interface CacheItem {

    }

    public static class RollbackItem implements CacheItem {
        String msg;

        public RollbackItem(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "RollbackItem{" + "msg=" + msg + '}';
        }

    }

    public static class CommitItem implements CacheItem {
        Group resultGroup;
        Position resultPosition;

        public CommitItem(Group resultGroup, Position resultPosition) {
            this.resultGroup = resultGroup.clone();
            this.resultPosition = resultPosition.clone();
        }

        public Group getResultGroup() {
            return resultGroup;
        }

        public Position getResultPosition() {
            return resultPosition;
        }

        @Override
        public String toString() {
            return "CommitItem{" + "pos=" + resultPosition + "; group=" + resultGroup + '}';
        }

    }
}
