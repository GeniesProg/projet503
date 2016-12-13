<?php
include("Histogramme.php");
 
// Constantes
define("LARGEUR", 500);                   // Largeur par défaut de l'image
define("HAUTEUR", 300);                   // Hauteur par défaut de l'image
 
// Récupération de la largeur et de la hauteur envoyées au script via la méthode GET


if (isset($_GET["json"])) {

    $json = str_replace("%", "\"", $_GET["json"]);
    $jsonobject = json_decode($json, true);
    $array  = array();

    $jsonarray = $jsonobject["questions"];
    for ($i = 0 ; $i < sizeof($jsonarray) ; $i++) {
        $question = array("nb" => 0, "reponses" => array());
        $reponses = $jsonarray[$i]["reps"];
        for ($j = 0 ; $j < sizeof($reponses) ; $j++) {
            $question["nb"] += $reponses[$j]["nb"];
            array_push($question["reponses"], $reponses[$j]["nb"]);
        }
        array_push($array, $question);
    }
}

$largeur = LARGEUR;
$hauteur = HAUTEUR;
 
// Création de l'histogramme
$histo = new Histogramme($largeur, $hauteur);
 
// Données quelconques pour l'histogramme
/*$donnees = array(array("nb" => 100, "reponses" => array(40, 30)),
                 array("nb" => 80, "reponses" => array(10, 20, 20)),
                 array("nb" => 90, "reponses" => array(40, 20, 10, 10)),
                 array("nb" => 60, "reponses" => array(10, 10, 40)),
                 array("nb" => 80, "reponses" => array(40, 0, 40)),
                 array("nb" => 100, "reponses" => array(0, 90)),
                 array("nb" => 40, "reponses" => array(10, 10, 10, 10)),
                 array("nb" => 160, "reponses" => array(10, 10, 40)));   */
$donnees = $array;
 
// Envoi de l'image
header("Content-type : image/png");
imagepng($histo->getImage($donnees));
?>