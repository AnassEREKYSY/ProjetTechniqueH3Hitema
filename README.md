# ProjetTechniqueH3Hitema

- API en Spring Boot avec une base de données en MySQL
- API qui gére une clone de Twitter sous le nom de Canard
- Cette API gére les utilisateurs, posts, likes, comments, messages et notification
- L'utilisateur peut créer un compte, se connecter, changer son profile, créé, like et commenté des poste et envoyé des messages

## Démarrer l'Application

Pour démarrer l'application, suivez ces étapes :

Prérequis :
1. Avoir Docker Descktop
2. Avoir Git 
3. Avoir le JDK 
4. Avoir un IDE

Démarage de l'application :

1. Clonez le dépôt : `https://github.com/AnassEREKYSY/ProjetTechniqueH3Hitema_BackEnd`
2. Lancer Docker descktop
4. Accédez au répertoire du projet : `cd votre-projet`
5. Lancer la commande :'cocker-compose up -d' pour créer le container de la base de données
6. Lier le container avec la base de données en utilisants les informations qui se trouves dans le fichier : "/src/main/resources/application.properties"
7. Lancez l'application
