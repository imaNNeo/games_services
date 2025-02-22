import Foundation

enum Method: String {
  case unlock = "unlock"
  case submitScore = "submitScore"
  case showLeaderboards = "showLeaderboards"
  case showAchievements = "showAchievements"
  case isSignedIn = "isSignedIn"
  case getPlayerID = "getPlayerID"
  case getPlayerName = "getPlayerName"
  case getPlayerScore = "getPlayerScore"
  case playerIsUnderage = "playerIsUnderage"
  case playerIsMultiplayerGamingRestricted = "playerIsMultiplayerGamingRestricted"
  case playerIsPersonalizedCommunicationRestricted = "playerIsPersonalizedCommunicationRestricted"
  case showAccessPoint = "showAccessPoint"
  case hideAccessPoint = "hideAccessPoint"
  case signIn = "signIn"
  case saveGame = "saveGame"
  case loadGame = "loadGame"
  case getSavedGames = "getSavedGames"
  case deleteGame = "deleteGame"
  case loadAchievements = "loadAchievements"
  case loadLeaderboardScores = "loadLeaderboardScores"
}
