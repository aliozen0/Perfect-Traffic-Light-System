// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getFirestore } from "firebase/firestore";
import { getAuth, GoogleAuthProvider } from "firebase/auth"; 

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBSLo0tYA0ALr2E4SgYVIo9-wQFQVntd6k",
  authDomain: "perfect-traffic-lights.firebaseapp.com",
  projectId: "perfect-traffic-lights",
  storageBucket: "perfect-traffic-lights.firebasestorage.app",
  messagingSenderId: "49179660734",
  appId: "1:49179660734:web:7df0368a3387ca3f9229ef",
  measurementId: "G-0EGFRNQQRE"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const db = getFirestore(app);
export const auth = getAuth(app);
export const provider = new GoogleAuthProvider();

export { app, analytics, db };