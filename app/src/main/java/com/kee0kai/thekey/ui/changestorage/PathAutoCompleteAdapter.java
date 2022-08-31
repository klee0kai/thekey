package com.kee0kai.thekey.ui.changestorage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.kee0kai.thekey.utils.android.UserShortPaths;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class PathAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final ItemFilter itemFilter = new ItemFilter();


    private ArrayList<String> pathes = new ArrayList<>();
    private String curDir = "";

    private void setPathes(ArrayList<String> pathes) {
        this.pathes = pathes;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return pathes != null ? pathes.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return curDir + pathes.get(position) + "/";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView != null ? convertView : LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        TextView tv = v.findViewById(android.R.id.text1);
        tv.setText(pathes.get(position));
        return v;
    }


    @Override
    public Filter getFilter() {
        return itemFilter;
    }


    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence c) {
            FilterResults results = new FilterResults();
            String searchText = c != null ? c.toString().toLowerCase() : null;
            if (searchText == null || searchText.length() == 0) {
                String[] roots = UserShortPaths.getRootPaths(true);
                ArrayList<String> aRoots = new ArrayList<>(Arrays.asList(roots));
                results.count = aRoots.size();
                results.values = new FilterPublishResult("", aRoots);
                return results;
            }


            String seatchAbsPath = UserShortPaths.absolutePath(searchText);
            boolean isDir = seatchAbsPath.charAt(seatchAbsPath.length() - 1) == '/';
            String searchFileName = isDir ? null : new File(seatchAbsPath).getName().toLowerCase();
            File parent = isDir ? new File(seatchAbsPath) : new File(seatchAbsPath).getParentFile();


            ArrayList<String> availableDirs = new ArrayList<>(
                    parent != null ?
                            Arrays.asList(parent.list(new IsDirFileFilter())) :
                            Arrays.asList(UserShortPaths.getRootPaths(true)));

            if (searchFileName != null && !searchFileName.isEmpty())
                for (int i = availableDirs.size() - 1; i >= 0; i--) {

                    if (!availableDirs.get(i).toLowerCase().contains(searchFileName))
                        availableDirs.remove(i);
                }

            results.count = availableDirs.size();
            results.values = new FilterPublishResult(parent != null ? UserShortPaths.shortPathName(parent.getAbsolutePath()).toString() + "/" : "", availableDirs);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values == null)
                return;

            setPathes(((FilterPublishResult) results.values).variants);
            curDir = ((FilterPublishResult) results.values).curPath;
        }
    }

    private static class FilterPublishResult {
        String curPath = "";
        ArrayList<String> variants;

        public FilterPublishResult(String curPath, ArrayList<String> variants) {
            this.curPath = curPath;
            this.variants = variants;
        }
    }

    private static class IsDirFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return new File(dir, name).isDirectory();
        }
    }
}


