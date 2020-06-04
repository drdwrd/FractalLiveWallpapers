package drwdrd.adev.realtimefractal.UI;


import android.view.MotionEvent;

import java.util.ArrayList;

import drwdrd.adev.engine.LogSystem;

public class Menu {

    public interface OnItemClickedListener {
        public void onItemClicked(Button item);
    }

    public class MenuItem {
        //button representing this item
        private Button button = null;
        //parent item
        private  MenuItem parentItem = null;
        //optional submenu
        private ArrayList<MenuItem> subMenu = new ArrayList<>();

        private MenuItem() {

        }

        private MenuItem(int id,String icon,String iconPressed,boolean toggleable) {
            subMenu.add(this);
            button = new Button(id,icon,iconPressed,toggleable);
        }

        public MenuItem add(int id,String icon) {
            MenuItem item = new MenuItem(id,icon,icon,false);
            item.parentItem = this;
            subMenu.add(item);
            return item;
        }

        public MenuItem add(int id,String icon,String iconPressed,boolean toggleable) {
            MenuItem item = new MenuItem(id,icon,iconPressed,toggleable);
            item.parentItem = this;
            subMenu.add(item);
            return item;
        }

        public ArrayList<MenuItem> getSubMenu() {
            return subMenu;
        }

        public Button getButton() {
            return button;
        }
    }

    //root item of menu
    private MenuItem rootMenuItem = null;
    private MenuItem currentMenuItem = null;
    private OnItemClickedListener onItemClickedListener= null;
    private MenuItem currentClickedItem = null;


    public Menu() {
        rootMenuItem = new MenuItem();
        currentMenuItem = rootMenuItem;
    }

    public MenuItem add(int id,String icon) {
        return rootMenuItem.add(id,icon);
    }

    public MenuItem add(int id,String icon,String iconPressed,boolean toggleable) {
        return rootMenuItem.add(id,icon,iconPressed,toggleable);
    }

    public MenuItem getCurrentMenu() {
        return currentMenuItem;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public boolean onTouchEvent(float x,float y,int action) {
        for(Menu.MenuItem item: currentMenuItem.getSubMenu()) {
            Button button = item.getButton();
            if(button.getRectangle().contains(x,y) == true) {
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        currentClickedItem = item;
                        button.setPressed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        button.setPressed(false);
                        if(currentClickedItem == item) {
                            if(button.isToggleable() == true) {
                                if(button.isChecked() == false) {
                                    button.setChecked(true);
                                } else {
                                    button.setChecked(false);
                                }
                            }
                            onItemClicked(currentClickedItem);
                        }
                        currentClickedItem = null;
                        break;
                }
                return true;
            }
        }
        return false;
/*        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (Menu.MenuItem item: currentMenuItem.getSubMenu()) {
                    Button button = item.getButton();
                    if (button.getRectangle().contains(x, y) == true) {
                        currentClickedItem = item;
                        button.setPressed(true);
                        return true;
                    }
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(currentClickedItem != null) {
                    Button button = currentClickedItem.getButton();
                    if(button.getRectangle().contains(x, y) == true) {
                        button.setPressed(false);
                        if (button.isToggleable() == true) {
                            if (button.isChecked() == false) {
                                button.setChecked(true);
                            } else {
                                button.setChecked(false);
                            }
                        }
                        onItemClicked(currentClickedItem);
                    } else {
                        button.setPressed(false);
                    }
                    currentClickedItem = null;
                    return true;
                }
                return false;
            default:
                for (Menu.MenuItem item: currentMenuItem.getSubMenu()) {
                    Button button = item.getButton();
                    if (button.getRectangle().contains(x, y) == true) {
                        return true;
                    }
                }
                return false;
        }*/
    }

    private void onItemClicked(MenuItem menuItem) {
        if(menuItem.getSubMenu().size() > 1) {
            LogSystem.debug("onItemClicked","up");
            if (menuItem.getButton().isChecked() == true) {
                currentMenuItem = menuItem;
            } else {
                if (menuItem.parentItem != null) {
                    currentMenuItem = menuItem.parentItem;
                    for(MenuItem item: menuItem.getSubMenu()) {
                        Button button = item.getButton();
                        if (button.isChecked() == true) {
                            item.getButton().setChecked(false);
                            onItemClicked(item);
                        }
                    }
                }
            }
        }
        if(onItemClickedListener != null) {
            onItemClickedListener.onItemClicked(menuItem.getButton());
        }
    }
}
