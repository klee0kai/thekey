package com.kee0kai.thekey.ui.notes.model;

import com.github.klee0kai.hummus.model.ICloneable;
import com.github.klee0kai.hummus.model.ISameModel;
import com.kee0kai.thekey.engine.model.DecryptedNote;

import java.util.Objects;


public class NoteItem implements ICloneable, ISameModel {
    public final long id;
    public final DecryptedNote decryptedNote;

    public NoteItem(long id, DecryptedNote decryptedNote) {
        this.id = id;
        this.decryptedNote = decryptedNote;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean isSame(Object ob) {
        return ob instanceof NoteItem && id == ((NoteItem) ob).id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteItem that = (NoteItem) o;
        return id == that.id && Objects.equals(decryptedNote, that.decryptedNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, decryptedNote);
    }
}
