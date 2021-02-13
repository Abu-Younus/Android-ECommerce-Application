import React, {useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import AppBar from '@material-ui/core/AppBar';
import CssBaseline from '@material-ui/core/CssBaseline';
import Toolbar from '@material-ui/core/Toolbar';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import {Category, Home, Phonelink, PowerSettingsNew, Settings, ShoppingCart} from "@material-ui/icons"
import icon from '../Media/icon.png'
import HomeFragment from '../Fragments/HomeFragment'
import ManageCategoryFragment from '../Fragments/ManageCategoryFragment';
import AddProductFragment from '../Fragments/AddProductFragment';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  drawerContainer: {
    overflow: 'auto',
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
}));

export default function ClippedDrawer() {
  const classes = useStyles();

  const [fragment, setFragment] = useState("HOME")

  const loadFragment = () => {
    switch (fragment) {
      case "HOME":
        return <HomeFragment />
      case "MANAGE_CATEGORY":
        return <ManageCategoryFragment />
      case "ADD_PRODUCT":
        return <AddProductFragment />
      default:
        break;
    }
  }

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
            <img src={icon} height="35px" />
        </Toolbar>
      </AppBar>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{
          paper: classes.drawerPaper,
        }}
      >
        <Toolbar />
        <div className={classes.drawerContainer}>
          <List>
            <ListItem button onClick={e=>setFragment("HOME")}>
                <ListItemIcon><Home /></ListItemIcon>
                <ListItemText primary="Home" />
            </ListItem>
            <ListItem button onClick={e=>setFragment("MANAGE_CATEGORY")}>
                <ListItemIcon><Category /></ListItemIcon>
                <ListItemText primary="Categories" />
            </ListItem>
            <ListItem button onClick={e=>setFragment("ADD_PRODUCT")}>
                <ListItemIcon><Phonelink /></ListItemIcon>
                <ListItemText primary="Products" />
            </ListItem>
            <ListItem button>
                <ListItemIcon><ShoppingCart /></ListItemIcon>
                <ListItemText primary="Orders" />
            </ListItem>
            <ListItem button>
                <ListItemIcon><Settings /></ListItemIcon>
                <ListItemText primary="Settings" />
            </ListItem>
            <Divider />
            <ListItem button>
                <ListItemIcon><PowerSettingsNew /></ListItemIcon>
                <ListItemText primary="Log Out" />
            </ListItem>
          </List>
          <Divider />
        </div>
      </Drawer>
      <main className={classes.content}>
        <Toolbar />
        {loadFragment()}
      </main>
    </div>
  );
}
