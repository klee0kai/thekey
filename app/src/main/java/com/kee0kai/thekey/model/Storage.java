package com.kee0kai.thekey.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.ISameModel;

import java.util.Objects;

public class Storage implements ISameModel, ICloneable, Parcelable, Comparable<Storage> {

    /**
     * полный путь к ранилищу
     */
    public String path;

    /**
     * имя хранилища
     */
    public String name;

    /**
     * описание хранилища
     */
    public String description;

    public Storage() {
    }

    public Storage(String path, String name, String description) {
        this.path = path;
        this.name = name;
        this.description = description;
    }


    protected Storage(Parcel in) {
        path = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<Storage> CREATOR = new Creator<Storage>() {
        @Override
        public Storage createFromParcel(Parcel in) {
            return new Storage(in);
        }

        @Override
        public Storage[] newArray(int size) {
            return new Storage[size];
        }
    };

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


    @Override
    public int compareTo(Storage o) {
        return path != null ? path.compareTo(o.path) : -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeString(description);
    }


}
