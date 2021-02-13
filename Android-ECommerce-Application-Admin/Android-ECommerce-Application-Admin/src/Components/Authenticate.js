import React, {useState} from 'react'
import { Redirect } from 'react-router-dom'
import { firebaseAuth } from '../firebase'

const Authenticate = (props) => {
    const [user, setUser] = useState(null)
    firebaseAuth.onAuthStateChanged(user=>{
        if(user) {
            setUser(true)
        } else {
            setUser(false)
        }
    })
    if(props.nonAuthenticate) {
        if(user==null) {
            return "Loading...";
        } else if(!user) {
            return props.children;
        } else if(user) {
            return <Redirect to="/" />
        }
    } else {
        if(user==null) {
            return "Loading...";
        } else if(user) {
            return props.children;
        } else if(!user) {
            return <Redirect to="/login" />
        }
    }
}

export default Authenticate
