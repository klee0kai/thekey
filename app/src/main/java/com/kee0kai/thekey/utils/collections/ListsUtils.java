package com.kee0kai.thekey.utils.collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListsUtils {

    public interface IEq<T> {
        boolean eq(T it1, T it2);
    }

    public interface IFilter<T> {
        boolean filt(int i, T it);
    }

    public interface IFormat<T, TOut> {
        TOut format(T it);
    }

    public interface IGroup<T, TOut> {
        int groupId(T it);

        List<TOut> group(List<T> lGroup);
    }

    public interface IJoin<T1, T2, TOut> {

        boolean isJoin(@NonNull T1 it1, @NonNull T2 it2);

        TOut join(@Nullable T1 it1, @Nullable T2 it2);

    }

    public static <T> List<T> filter(List<T> list, IFilter<T> filtHelper) {
        if (list == null) return null;
        LinkedList<T> out = new LinkedList<>();
        int i = 0;
        for (T it : list)
            if (filtHelper.filt(i++, it))
                out.add(it);
        return out;
    }


    public static <T> T first(List<T> list, IFilter<T> filtHelper) {
        if (list == null) return null;
        int i = 0;
        for (T it : list)
            if (filtHelper.filt(i++, it))
                return it;
        return null;
    }


    public static <T> int index(List<T> list, IFilter<T> filtHelper) {
        if (list == null) return -1;
        int i = 0;
        for (T it : list)
            if (filtHelper.filt(i++, it))
                return --i;
        return -1;
    }


    public static <T> boolean contains(List<T> list, IFilter<T> filtHelper) {
        return index(list, filtHelper) >= 0;
    }


    public static <T> List<T> removeDoubles(List<T> list, IEq<T> eqHelper) {
        if (list == null) return null;
        LinkedList<T> out = new LinkedList<>();
        for (T it : list) {
            boolean contains = false;
            for (T o : out) {
                if (eqHelper.eq(o, it)) {
                    contains = true;
                    break;
                }
            }
            if (!contains)
                out.add(it);
        }
        return out;
    }

    public static <T, TOut> List<TOut> format(List<T> list, IFormat<T, TOut> formatHelper) {
        if (list == null) return null;
        List<TOut> out = (list instanceof ArrayList) ? new ArrayList<>(list.size()) : new LinkedList<>();
        for (T it : list)
            out.add(formatHelper.format(it));
        return out;
    }


    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }


    public static <T, TOut> List<TOut> group(List<T> list, IGroup<T, TOut> groupHelper) {
        if (list == null) return null;
        ExtSparseArray<LinkedList<T>> groups = new ExtSparseArray<>();
        for (T it : list) {
            int groupId = groupHelper.groupId(it);
            LinkedList<T> gr = groups.get(groupId);
            if (gr == null) {
                gr = new LinkedList<>();
                groups.put(groupId, gr);
            }
            gr.add(it);
        }

        List<TOut> out = new LinkedList<>();
        for (int i = 0; i < groups.size(); i++) {
            out.addAll(groupHelper.group(groups.valueAt(i)));
        }
        return out;
    }

    public static <T1, T2, TOut> List<TOut> leftJoin(List<T1> l1, List<T2> l2, IJoin<T1, T2, TOut> joinHelper) {
        if (l1 == null) return null;
        LinkedList<TOut> out = new LinkedList<>();
        for (T1 it1 : l1) {
            if (it1 == null) continue;
            boolean added = false;
            if (l2 != null)
                for (T2 it2 : l2)
                    if (it2 != null && joinHelper.isJoin(it1, it2)) {
                        out.add(joinHelper.join(it1, it2));
                        added = true;
                        break;
                    }
            if (!added)
                out.add(joinHelper.join(it1, null));
        }
        return out;
    }

    public static <T1, T2, TOut> List<TOut> innerJoin(List<T1> l1, List<T2> l2, boolean multiToMulti, IJoin<T1, T2, TOut> joinHelper) {
        if (l1 == null || l2 == null) return null;
        LinkedList<TOut> out = new LinkedList<>();
        for (T1 it1 : l1) {
            if (it1 == null) continue;
            for (T2 it2 : l2)
                if (it2 != null && joinHelper.isJoin(it1, it2)) {
                    out.add(joinHelper.join(it1, it2));
                    if (!multiToMulti) break;
                }
        }
        return out;
    }
}
