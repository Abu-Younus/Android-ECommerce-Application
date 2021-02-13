import * as firebase from "firebase/app";
import "firebase/analytics";
import "firebase/auth";
import "firebase/firestore";
import "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyB6AYEjOgy9eAja-Zv4vHh_AjK4qYHtAy0",
  authDomain: "sohoz-bazar.firebaseapp.com",
  databaseURL: "https://sohoz-bazar.firebaseio.com",
  projectId: "sohoz-bazar",
  storageBucket: "sohoz-bazar.appspot.com",
  messagingSenderId: "138925982214",
  appId: "1:138925982214:web:d07b325f86e6af447a3987",
  measurementId: "G-2DM0PBTVCV",
};

firebase.initializeApp(firebaseConfig);

export const firebaseAuth = firebase.auth();
export const firebaseFirestore = firebase.firestore();
export const storageRef = firebase.storage().ref();

export default firebase;