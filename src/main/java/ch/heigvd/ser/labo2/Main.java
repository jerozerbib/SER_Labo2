/*
 -----------------------------------------------------------------------------------
 Laboratoire : SER - Laboratoire 2
 Fichier     : main.java
 Auteur(s)   : Jeremy Zerbib, Guillaume Laubscher, Julien Quartier
 Date        : 14/04/2019
 But         : Cree plusieurs fichier PGN permettant de jouer des parties d'echecs
 Remarque(s) :
                - Parse un fichier de façon a pouvoir lire des parties d'echec suivant une DTD en utilisant JDOM2
                - Nous ne tenons pas compte du cas ou un coup n'est pas un roque ou un deplacement
                    car cela n'arrive pas et ne peut pas arriver
 -----------------------------------------------------------------------------------
*/


package ch.heigvd.ser.labo2;

import ch.heigvd.ser.labo2.coups.*;
import org.jdom2.*; // Librairie à utiliser !
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

class Main {

    public static void main(String ... args) throws Exception {
        int i = 0;
        final String filename  = "partie_%d.PGN" ;

        final String xmlFile = "tournois_fse.xml";

        SAXBuilder builder = new SAXBuilder();
        Document doc       = builder.build(new File(xmlFile));

        Element root       = doc.getRootElement();
        Element tournois   = root.getChild("tournois");

        //Parcours des tournois
        for (Element tournoi : tournois.getChildren()){
            //Prend tous les parties d'un tournoi
            Element parties = tournoi.getChild("parties");

            //Parcours des parties
            for (Element partie : parties.getChildren()){
                //Prend tous les coups d'une partie
                Element coups = partie.getChild("coups");

                //Creation de l'ecrivain pour creer les fichier PGN
                PrintWriter pw = new PrintWriter(new FileWriter(String.format(filename, i++)));

                //Compteur de tour
                int tour = 1;
                //Parcours des coups joues
                for (Element coup : coups.getChildren()){
                    //Prend un deplacement pour un coup
                    Element deplacement = coup.getChild("deplacement");
                    //Appel de l'interface convertissableEnPGN pour pouvoir faire un Deplacement a la fin du parcours
                    ConvertissableEnPGN convertissableEnPGN;

                    //S'il n'y a pas de deplacement dans ce coup, il y a forcement un roque.
                    if (deplacement != null){
                        //Parsing de tous les champs necessaire a la creation d'un coup
                        String piece       = deplacement.getAttributeValue("piece");
                        String arrivee     = deplacement.getAttributeValue("case_arrivee");
                        String depart      = deplacement.getAttributeValue("case_depart");
                        String elimination = deplacement.getAttributeValue("elimination");
                        String promotion   = deplacement.getAttributeValue("promotion");
                        String coupSpecial = coup.getAttributeValue("coup_special");

                        //Creation des cases.
                        //Si la case d'arrivee est nulle, nous avons un probleme donc nous renvoyons une erreur.
                        Case Cdepart  = isCaseFieldNull(depart);
                        Case Carrivee = isCaseFieldNull(arrivee);
                        if (Carrivee == null){
                            throw new NullPointerException("La case arrivee doit etre renseignee ! Ce n'est pas le cas");
                        }

                        //Creation des types de pieces.
                        TypePiece eliminationT = isPieceFieldNull(elimination);
                        TypePiece promotionT = isPieceFieldNull(promotion);

                        //Creation du coup special
                        CoupSpecial coupSpecT = isSpecialMoveFieldNull(coupSpecial);


                        //Creation du deplacement
                        convertissableEnPGN = new Deplacement(
                                TypePiece.valueOf(piece),
                                eliminationT,
                                promotionT,
                                coupSpecT,
                                Cdepart,
                                Carrivee);
                    } else{ //Cas ou le coup joue est un roque
                        Element roque      = coup.getChild("roque");
                        String type        = roque.getAttributeValue("type");
                        String coupSpecial = coup.getAttributeValue("coup_special");


                        TypeRoque typeRoque;
                        switch (type){
                            case "petit_roque":
                                typeRoque = TypeRoque.PETIT;
                                break;
                            case "grand_roque":
                                typeRoque = TypeRoque.GRAND;
                                break;
                            default:
                                throw new Exception("Type de roque non supporte !");
                        }

                        //Creation du coup special
                        CoupSpecial coupSpecT = isSpecialMoveFieldNull(coupSpecial);

                        //Creation du roque
                        convertissableEnPGN = new Roque(coupSpecT, typeRoque);

                    }

                    //Increment du tour mais affichage de ce dernier seulement une fois sur deux
                    ++tour;
                    if (tour%2 != 1){
                        pw.print(tour / 2 + " ");
                        pw.print(convertissableEnPGN.notationPGN() + " ");
                    } else {
                        pw.println(convertissableEnPGN.notationPGN());
                    }


                }

                //Vidage de l'ecrivain apres une partie
                pw.flush();
            }
        }
    }

    /**
     * Methode permettant de savoir si une piece a une valeur null
     * @param parsedPiece String permettant de savoir quelle piece a ete joue
     * @return La piece dans son type
     */
    private static TypePiece isPieceFieldNull(String parsedPiece){
        if (parsedPiece == null){
            return null;
        }
        return TypePiece.valueOf(parsedPiece);
    }


    /**
     * Methode permettant de savoir si une case a une valeur null
     * @param parsedCase String permettant de savoir quelle case est atteinte ou quelle est la case de depart
     * @return La case dans son type
     */
    private static Case isCaseFieldNull(String parsedCase){
        if (parsedCase != null && (parsedCase.length() == 2)){
            return new Case(parsedCase.charAt(0), parsedCase.charAt(1));
        }
        return null;
    }



    /**
     * Methode permettant de savoir si un CoupSpecial a une valeur null
     * @param parsedCoupSpecial String permettant de savoir quel coup specia a ete joue
     * @return Le coup special dans son type
     */
    private static CoupSpecial isSpecialMoveFieldNull(String parsedCoupSpecial){
        if (parsedCoupSpecial == null){
            return null;
        } else{
            return CoupSpecial.valueOf(parsedCoupSpecial.toUpperCase());
        }
    }

}