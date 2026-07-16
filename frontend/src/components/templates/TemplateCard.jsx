import Card from "../ui/Card";
import Button from "../ui/Button";

function TemplateCard({

    template,

    onDelete

}) {

    return (

        <Card>

            <h3>{template.name}</h3>

            <p>

                Type: {template.type}

            </p>

            <Button

                variant="danger"

                onClick={() => onDelete(template.name)}

            >

                Delete

            </Button>

        </Card>

    );

}

export default TemplateCard;