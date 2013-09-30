package net.dmi3.scriptexec.entity;

import net.dmi3.scriptexec.infrastructure.Lang;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class ScriptDTO {
    public Long id;
    public Lang lang;
    public String script;
    public String result;
    public Status status;

    public ScriptDTO() {
    }

    public ScriptDTO(Lang lang, String script) {
        this.lang = lang;
        this.script = script;
    }


    public ScriptDTO copy() {
        ScriptDTO copy = new ScriptDTO();
        copy.id = id;
        copy.lang = lang;
        copy.script = script;
        copy.result = result;
        copy.status = status;

        return copy;
    }

    public String getResult() {
        return result;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
