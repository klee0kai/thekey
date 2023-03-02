package com.kee0kai.thekey.ui.fileprovider.model;


import com.github.klee0kai.hummus.model.ICloneable;
import com.github.klee0kai.hummus.model.ISameModel;

import java.util.Objects;

public class FileItem implements ISameModel, ICloneable, Comparable<FileItem> {

    public final String name;
    public final boolean isFile;

    public FileItem(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean isSame(Object ob) {
        return equals(ob);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileItem fileItem = (FileItem) o;
        return isFile == fileItem.isFile && Objects.equals(name, fileItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isFile);
    }

    @Override
    public int compareTo(FileItem o) {
        int c1 = Boolean.compare(isFile, o.isFile);
        if (c1 != 0) return c1;
        return name != null ? name.compareTo(o.name) : -1;
    }
}
