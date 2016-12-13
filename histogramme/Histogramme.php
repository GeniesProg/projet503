<?php
define("BORD_HAUT", 10);                     
define("BORD_BAS", 10);                      
define("BORD_DROITE", 10);
define("BORD_GAUCHE", 10);
define("POLICE_AXE_X", 5);
define("POLICE_AXE_Y", 1);
define("ESPACE_BARRE", 20);
define("LARGEUR_AXE_Y", 30);
define("HAUTEUR_AXE_X", 20);
define("ESPACE_AXE", 5);
define("TAILLE_TEXTE_AXE_X", 10);
define("TAILLE_TEXTE_AXE_Y", 5);
 
class Histogramme {
 
    private $largeur;
    private $hauteur;
 
    /**
     * Construit un histogramme.
     * @param largeur la largeur
     * @param hauteur la hauteur
     */
    function __construct($largeur, $hauteur) {
		$this->largeur = $largeur;
        $this->hauteur = $hauteur;
	} 
 
    /**
     * Calcule la hauteur et la largeur d'un texte.
     * @param image l'image
     * @param texte le texte
     * @param police le numéro de la police
     * @return un tableau contenant la largeur et la hauteur
     **/
    public static function getTaille($image, $texte, $police) {
        return array(imagefontwidth($police) * strlen($texte), imagefontheight($police));
    }    
 
    /** 
     * Trace les axes.
     * @param image l'image de l'histogramme
     * @param nbBarres le nombre de barres     
     * @param largeurBarre la largeur des barres
     */
    function traceAxes($image, $nbBarres, $largeurBarre) {
        $couleurAxe = imagecolorallocate($image, 255, 255, 255);
 
        // Affichage des axes
        imageline($image, 
                  BORD_GAUCHE + LARGEUR_AXE_Y - ESPACE_AXE, 
                  BORD_HAUT, 
                  BORD_GAUCHE + LARGEUR_AXE_Y - ESPACE_AXE,
                  $this->hauteur - BORD_BAS - HAUTEUR_AXE_X + ESPACE_AXE,
                  $couleurAxe);
        imageline($image, 
                  BORD_GAUCHE + LARGEUR_AXE_Y - ESPACE_AXE,
                  $this->hauteur - BORD_BAS - HAUTEUR_AXE_X + ESPACE_AXE,
                  $this->largeur - BORD_DROITE,
                  $this->hauteur - BORD_BAS - HAUTEUR_AXE_X + ESPACE_AXE,
                  $couleurAxe);
 
        // Affichage de la légende de l'axe des ordonnées
        $texte = "0%";
        list($largeurTexte, $hauteurTexte) = Histogramme::getTaille($image, $texte, POLICE_AXE_Y);
        imagestring($image, POLICE_AXE_Y,
                    BORD_GAUCHE + LARGEUR_AXE_Y - $largeurTexte - ESPACE_AXE * 2,
                    $this->hauteur - BORD_BAS - HAUTEUR_AXE_X - $hauteurTexte,
                    $texte, $couleurAxe);
 
        $texte = "50%";
        list($largeurTexte, $hauteurTexte) = Histogramme::getTaille($image, $texte, POLICE_AXE_Y);
        imagestring($image, POLICE_AXE_Y,
                    BORD_GAUCHE + LARGEUR_AXE_Y - $largeurTexte - ESPACE_AXE * 2,
                    BORD_HAUT + ($this->hauteur - BORD_HAUT - BORD_BAS - HAUTEUR_AXE_X - $hauteurTexte) / 2,
                    $texte, $couleurAxe);
 
        $texte = "100%";
        list($largeurTexte, $hauteurTexte) = Histogramme::getTaille($image, $texte, POLICE_AXE_Y);
        imagestring($image, POLICE_AXE_Y, 
                    BORD_GAUCHE + LARGEUR_AXE_Y - $largeurTexte - ESPACE_AXE * 2,
                    BORD_HAUT, 
                    $texte, $couleurAxe);
 
        // Affichage du numéro de la question
        for($i = 0; $i < $nbBarres; $i++) {
            $texte = "".($i + 1);
            list($largeurTexte, $hauteurTexte) = Histogramme::getTaille($image, $texte, POLICE_AXE_X);
            imagestring($image, POLICE_AXE_X,
                        BORD_GAUCHE + LARGEUR_AXE_Y + $i * ($largeurBarre + ESPACE_BARRE) + ($largeurBarre - $largeurTexte) / 2,
                        $this->hauteur - BORD_BAS - HAUTEUR_AXE_X + 2 * ESPACE_AXE,
                        $texte, $couleurAxe);                     
        }
    }
 
    /**
     * Construit l'image à partir de données.
     * @param donnees les données
     * @return l'image créée
     */
    function getImage($donnees) {
        // Création de l'image
        $image = imagecreatetruecolor($this->largeur, $this->hauteur);
 
        // Définition des couleurs : le texte et les couleurs pour l'histogramme
        $couleurs = array(imagecolorallocate($image, 255, 255, 0),     // Couleur pour la réponse A
                          imagecolorallocate($image, 255, 128, 0),     // Couleur pour la réponse B
                          imagecolorallocate($image, 255, 130, 130),   // Couleur pour la réponse C
                          imagecolorallocate($image, 255, 0, 0),       // Couleur pour la réponse D
                          imagecolorallocate($image, 150, 150, 150));  // Couleur pour autres réponses
 
        // Calcul des dimensions des barres
        $nbBarres = sizeof($donnees);
        $largeurBarre = ($this->largeur - BORD_DROITE - LARGEUR_AXE_Y - BORD_GAUCHE - ESPACE_BARRE * ($nbBarres - 1)) / $nbBarres;
        $hauteurBarre = $this->hauteur - BORD_BAS - BORD_HAUT - HAUTEUR_AXE_X;
 
        // Affichage des axes et de la légende
        $this->traceAxes($image, $nbBarres, $largeurBarre);
 
        // Affichage des barres
        for($i = 0; $i < $nbBarres; $i++) {
            $max = $donnees[$i]['nb'];
            $total = 0;
 
            // Affichage des différents carrés correspondant aux réponses
            for($j = 0; $j < sizeof($donnees[$i]['reponses']); $j++) {
                $hauteurRect = $donnees[$i]['reponses'][$j] * $hauteurBarre / $max;
                imagefilledrectangle($image, 
                                     BORD_GAUCHE + LARGEUR_AXE_Y + $i * ($largeurBarre + ESPACE_BARRE),
                                     $this->hauteur - BORD_BAS - HAUTEUR_AXE_X - $total * $hauteurBarre / $max,
                                     BORD_GAUCHE + LARGEUR_AXE_Y + $i * ($largeurBarre + ESPACE_BARRE) + $largeurBarre,
                                     $this->hauteur - BORD_BAS - HAUTEUR_AXE_X - $hauteurRect - $total * $hauteurBarre / $max,
                                     $couleurs[$j]);            
                $total += $donnees[$i]['reponses'][$j];
            }
 
            // Affichage du cadre pour le reste des questions
            if($total != $max) {
                $hauteurRect = ($max - $total) * $hauteurBarre / $max;
                imagefilledrectangle($image, 
                                     BORD_GAUCHE + LARGEUR_AXE_Y + $i * ($largeurBarre + ESPACE_BARRE),
                                     $this->hauteur - BORD_BAS - HAUTEUR_AXE_X - $total * $hauteurBarre / $max,
                                     BORD_GAUCHE + LARGEUR_AXE_Y + $i * ($largeurBarre + ESPACE_BARRE) + $largeurBarre,
                                     $this->hauteur - BORD_BAS - $hauteurRect - HAUTEUR_AXE_X - $total * $hauteurBarre / $max,
                                     $couleurs[4]);
            }
        }
 
        return $image;
    }    
}
?>