import React, { useEffect, useState } from "react";
import axios from "axios";

function VerifyEmail() {
    const [message, setMessage] = useState("");
    const baseURL=process.env.REACT_APP_BACK_END_BASE_URL;
    useEffect(() => {
        const token = new URLSearchParams(window.location.search).get("token");
        axios
            .get(`${baseURL}/api/verify-email?token=${token}`)
            .then(() => alert("Email verified successfully!"))
            .catch(() => alert("Invalid or expired token."));
    }, []);

    return <div>
    
    <h1>Verification</h1>
    
    </div>;
}

export default VerifyEmail;
