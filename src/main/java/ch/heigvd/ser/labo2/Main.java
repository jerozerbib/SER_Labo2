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
            Element parties = tournoi.getChild("parties");
            for (Element partie : parties.getChildren()){
                Element coups = partie.getChild("coups");
                PrintWriter pw = new PrintWriter(new FileWriter(String.format(filename, i++)));
                int tour = 1;
                for (Element coup : coups.getChildren()){
                    Element deplacement = coup.getChild("deplacement");
                    ConvertissableEnPGN convertissableEnPGN;
                    if (deplacement != null){
                        String piece = deplacement.getAttributeValue("piece");
                        String arrivee = deplacement.getAttributeValue("case_arrivee");
                        String depart = deplacement.getAttributeValue("case_depart");
                        String elimination = deplacement.getAttributeValue("elimination");
                        String promotion = deplacement.getAttributeValue("promotion");
                        String coupSpecial = coup.getAttributeValue("coup_special");

                        Case Cdepart = null;
                        if (depart != null && (depart.length() == 2)){
                            Cdepart = new Case(depart.charAt(0), depart.charAt(1));
                        }

                        Case Carrivee = null;
                        if (arrivee != null && (arrivee.length() == 2)){
                            Carrivee = new Case(arrivee.charAt(0), arrivee.charAt(1));
                        } else {
                            throw new Error("Arrivee n'est pas definie ! Erreur !");
                        }

                        TypePiece eliminationT;
                        if (elimination == null){
                            eliminationT = null;
                        } else {
                            eliminationT = TypePiece.valueOf(elimination);
                        }


                        TypePiece promotionT;
                        if (promotion == null){
                            promotionT = null;
                        } else {
                            promotionT = TypePiece.valueOf(promotion);
                        }

                        CoupSpecial coupSpecT;
                        if (coupSpecial == null){
                            coupSpecT = null;
                        } else {
                            coupSpecT = CoupSpecial.valueOf(coupSpecial.toUpperCase());
                        }

                        convertissableEnPGN = new Deplacement(
                                TypePiece.valueOf(piece),
                                eliminationT,
                                promotionT,
                                coupSpecT,
                                Cdepart,
                                Carrivee);
                    } else {
                        Element roque = coup.getChild("roque");
                        //Roque(CoupSpecial coupSpecial, TypeRoque typeRoque)
                        String type = roque.getAttributeValue("type");
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

                        CoupSpecial coupSpecT;
                        if (coupSpecial == null){
                            coupSpecT = null;
                        } else{
                            coupSpecT = CoupSpecial.valueOf(coupSpecial.toUpperCase());
                        }


                        convertissableEnPGN = new Roque(coupSpecT, typeRoque);

                    }

                    ++tour;
                    if (tour%2 != 1){
                        pw.print(tour / 2 + " ");
                        pw.print(convertissableEnPGN.notationPGN() + " ");
                    } else {
                        pw.println(convertissableEnPGN.notationPGN());
                    }


                }
                pw.flush();
            }
            ++i;
        }




//        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//
//
//        try(BufferedReader br = new BufferedReader(new FileReader(xmlFile))){
//            final DocumentBuilder builder = factory.newDocumentBuilder();
//            final Document document       = builder.newDocument();
//
//            //Balise <Cities>
//            final Element allCities = document.createElement("Cities");
//            document.appendChild(allCities);
//
//
//            while ((line = br.readLine()) != null) {
//                // use comma as separator
//                parseTable = line.split(parseBy);
//
//                //Toutes les balises ci-dessous sont crées a ce moment du code mais nous voulons les rajouter a la balise
//                //racine seulement si nous ne sommes pas a la premiere ligne.
//
//                //Commentaire <!--City/State-->
//                final Comment comment = document.createComment(parseTable[indexCity] + "/" + parseTable[indexState]);
//
//                //Balise <City>
//                final Element city = document.createElement("City");
//
//                //Balise <Name>
//                final Element name = document.createElement("Name");
//
//                //Balise <State>
//                final Element state = document.createElement("State");
//
//                //Balise <Coordinates>
//                final Element coord = document.createElement("Coordinates");
//                coord.appendChild(document.createTextNode(coordinates));
//                city.appendChild(coord);
//            }
//
//
//            //Code pris dans l'exemple du cours
//            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            final Transformer transformer               = transformerFactory.newTransformer();
//            final DOMSource source                      = new DOMSource(document);
//            final StreamResult sortie                   = new StreamResult(new File("src/cities.xml"));
//
//            transformer.setOutputProperty(VERSION,    "1.0");
//            transformer.setOutputProperty(ENCODING,   "UTF-8");
//            transformer.setOutputProperty(STANDALONE, "yes");
//
//            transformer.setOutputProperty(INDENT,                                      "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
//
//            transformer.transform(source, sortie);
//
//
//
//
//        }catch (ParserConfigurationException | TransformerException | IOException e){
//            e.printStackTrace();
//        }

    }

}