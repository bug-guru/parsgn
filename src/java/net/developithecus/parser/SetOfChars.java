/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class SetOfChars {
    private TreeSet<Entry> entries = new TreeSet<Entry>();

    public SetOfChars includeAll() {
        entries.clear();
        entries.add(new Entry(Character.MIN_VALUE, Character.MAX_VALUE));
        return this;
    }

    public SetOfChars excludeAll() {
        entries.clear();
        return this;
    }

    public SetOfChars exclude(char ch) {
        return exclude(ch, ch);
    }

    public SetOfChars exclude(SetOfChars set) {
        for (Entry e : set.entries) {
            exclude(e);
        }
        return this;
    }

    public SetOfChars include(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            include(chars.charAt(i));
        }
        return this;
    }

    public SetOfChars exclude(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            exclude(chars.charAt(i));
        }
        return this;
    }

    public SetOfChars include(SetOfChars set) {
        for (Entry e : set.entries) {
            include(e);
        }
        return this;
    }

    public SetOfChars include(char ch) {
        return include(ch, ch);
    }

    public SetOfChars include(char start, char end) {
        Entry e = new Entry(start, end);
        include(e);
        return this;
    }

    public SetOfChars exclude(char start, char end) {
        Entry e = new Entry(start, end);
        exclude(e);
        return this;
    }

    public boolean contains(char ch) {
        return container(ch) != null;
    }

    private void exclude(Entry entry) {
        Iterator<Entry> i = entries.iterator();
        Entry toAdd = null;
        while (i.hasNext()) {
            Entry e = i.next();
            if (entry.subset(e)) {
                i.remove();
            } else if (e.contains(entry.start) && (!e.contains(entry.end) || e.end == entry.end)) {
                e.end = (char) (entry.start - 1);
            } else if ((!e.contains(entry.start) || e.start == entry.start) && e.contains(entry.end)) {
                e.start = (char) (entry.end + 1);
            } else if (e.contains(entry.start) && e.contains(entry.end)) {
                toAdd = new Entry((char) (entry.end + 1), e.end);
                e.end = (char) (entry.start - 1);
                break;
            }
        }
        if (toAdd != null) {
            entries.add(toAdd);
        }
    }

    private void include(Entry entry) {
        Iterator<Entry> i = entries.iterator();
        while (i.hasNext()) {
            Entry e = i.next();
            if (e.subset(entry)) {
                return;
            } else if (entry.subset(e)) {
                i.remove();
            } else if (e.intersects(entry) || e.isNeighbours(entry)) {
                if (e.start < entry.start) {
                    entry.start = e.start;
                }
                if (e.end > entry.end) {
                    entry.end = e.end;
                }
                i.remove();
            }
        }
        entries.add(entry);
    }


    private Entry container(char ch) {
        for (Entry e : entries) {
            if (e.contains(ch)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Entry e : entries) {
            result.append(e);
        }
        return result.toString();
    }

    private class Entry implements Comparable<Entry> {
        private char start;
        private char end;

        public Entry(char start, char end) {
            if (start > end) {
                this.start = end;
                this.end = start;
            } else {
                this.start = start;
                this.end = end;
            }
        }

        public Entry(int start, int end) {
            this((char) start, (char) end);
        }

        boolean contains(char ch) {
            return start <= ch && end >= ch;
        }

        boolean isNeighbours(Entry o) {
            return start - o.end == 1 || o.start - end == 1;
        }

        boolean intersects(Entry o) {
            return start <= o.start && end >= o.start ||
                    start <= o.end && end >= o.end;
        }

        // checks if parameter is subset of this entry
        boolean subset(Entry o) {
            return start <= o.start && end >= o.start &&
                    start <= o.end && end >= o.end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            if (this.start != other.start) {
                return false;
            }
            if (this.end != other.end) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 41 * hash + this.start;
            hash = 41 * hash + this.end;
            return hash;
        }

        @Override
        public int compareTo(Entry o) {
            int result = start - o.start;
            if (result == 0) {
                result = end - o.end;
            }
            return result;
        }

        String toString(char ch) {
            StringBuilder sb = new StringBuilder("0000");
            String h = Integer.toHexString(ch);
            sb.setLength(sb.length() - h.length());
            sb.append(h);
            return sb.toString();
        }

        @Override
        public String toString() {
            if (start == end) {
                return toString(start) + ";";
            } else {
                return toString(start) + "-" + toString(end) + ";";
            }
        }


    }
}
