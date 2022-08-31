package com.kee0kai.thekey.model;

import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.ISameModel;

import java.util.Objects;

public class Storage implements ISameModel, ICloneable {
    /**
     * полный путь к ранилищу
     */
    public final String path;

    /**
     * имя хранилища
     */
    public final String name;

    /**
     * описание хранилища
     */
    public final String description;

    public Storage(String path, String name, String description) {
        this.path = path;
        this.name = name;
        this.description = description;
    }


    @Override
    public boolean isSame(Object ob) {
        return ob instanceof Storage && Objects.equals(path, ((Storage) ob).path);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Storage storage = (Storage) o;
        return Objects.equals(path, storage.path) &&
                Objects.equals(name, storage.name) &&
                Objects.equals(description, storage.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, description);
    }


}
