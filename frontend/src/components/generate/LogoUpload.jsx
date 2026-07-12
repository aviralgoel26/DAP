import { useRef, useState } from "react";
import { UploadCloud } from "lucide-react";

function LogoUpload({logo,onChange}) {

    const inputRef = useRef(null);

    const [file, setFile] = useState(null);

    const handleSelect = (event) => {

        if (event.target.files.length > 0) {

            onChange(event.target.files[0]);

        }

    };

    return (

        <div className="logo-upload">

            <h3>Company Logo</h3>

            <div
                className="upload-box"
                onClick={() => inputRef.current.click()}
            >

                <UploadCloud size={36} />

                <p>

                    {

                        file
                            ? file.name
                            : "Click to upload PNG/JPG"

                    }

                </p>

            </div>

            <input
                ref={inputRef}
                type="file"
                hidden
                accept=".png,.jpg,.jpeg"
                onChange={handleSelect}
            />

        </div>

    );

}

export default LogoUpload;