package org.jabref.logic.citationkeypattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.jabref.model.entry.types.EntryType;

/**
 * A small table, where an entry type is associated with a Bibtex key pattern (an
 * <code>ArrayList</code>). A parent CitationKeyPattern can be set.
 */
public abstract class AbstractCitationKeyPattern {

    protected List<String> defaultPattern = new ArrayList<>();

    protected Map<EntryType, List<String>> data = new HashMap<>();

    /**
     * This method takes a string of the form [field1]spacer[field2]spacer[field3]..., where the fields are the
     * (required) fields of a BibTex entry. The string is split into fields and spacers by recognizing the [ and ].
     *
     * @param bibtexKeyPattern a <code>String</code>
     * @return an <code>ArrayList</code> The first item of the list is a string representation of the key pattern (the
     * parameter), the remaining items are the fields
     */
    public static List<String> split(String bibtexKeyPattern) {
        // A holder for fields of the entry to be used for the key
        List<String> fieldList = new ArrayList<>();

        // Before we do anything, we add the parameter to the ArrayLIst
        fieldList.add(bibtexKeyPattern);

        StringTokenizer tok = new StringTokenizer(bibtexKeyPattern, "[]", true);
        while (tok.hasMoreTokens()) {
            fieldList.add(tok.nextToken());
        }
        return fieldList;
    }

    public void addCitationKeyPattern(EntryType type, String pattern) {
        data.put(type, AbstractCitationKeyPattern.split(pattern));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractCitationKeyPattern{");
        sb.append("defaultPattern=").append(defaultPattern);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        AbstractCitationKeyPattern that = (AbstractCitationKeyPattern) o;
        return Objects.equals(defaultPattern, that.defaultPattern) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultPattern, data);
    }

    /**
     * Gets an object for a desired key from this CitationKeyPattern or one of it's parents (in the case of
     * DatabaseCitationKeyPattern). This method first tries to obtain the object from this CitationKeyPattern via the
     * <code>get</code> method of <code>Hashtable</code>. If this fails, we try the default.<br /> If that fails, we try
     * the parent.<br /> If that fails, we return the DEFAULT_LABELPATTERN<br />
     *
     * @param entryType a <code>String</code>
     * @return the list of Strings for the given key. First entry: the complete key
     */
    public List<String> getValue(EntryType entryType) {
        List<String> result = data.get(entryType);
        //  Test to see if we found anything
        if (result == null) {
            // check default value
            result = getDefaultValue();
            if (result == null || result.isEmpty()) {
                // we are the "last" to ask
                // we don't have anything left
                return getLastLevelCitationKeyPattern(entryType);
            }
        }
        return result;
    }

    /**
     * Checks whether this pattern is customized or the default value.
     */
    public final boolean isDefaultValue(EntryType entryType) {
        return data.get(entryType) == null;
    }

    /**
     * This method is called "...Value" to be in line with the other methods
     *
     * @return null if not available.
     */
    public List<String> getDefaultValue() {
        return this.defaultPattern;
    }

    /**
     * Sets the DEFAULT PATTERN for this key pattern
     *
     * @param bibtexKeyPattern the pattern to store
     */
    public void setDefaultValue(String bibtexKeyPattern) {
        Objects.requireNonNull(bibtexKeyPattern);
        this.defaultPattern = AbstractCitationKeyPattern.split(bibtexKeyPattern);
    }

    public Set<EntryType> getAllKeys() {
        return data.keySet();
    }

    public Map<EntryType, List<String>> getPatterns() {
        return data.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public abstract List<String> getLastLevelCitationKeyPattern(EntryType key);
}
