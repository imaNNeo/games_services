package com.abedalkareem.games_services

import android.app.Activity
import com.abedalkareem.games_services.models.Method
import com.abedalkareem.games_services.models.methodsFrom
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


private const val CHANNEL_NAME = "games_services"

class GamesServicesPlugin : FlutterPlugin,
  MethodCallHandler, ActivityAware {

  //region Variables
  private var activity: Activity? = null
  private var channel: MethodChannel? = null
  private var activityPluginBinding: ActivityPluginBinding? = null
  private var leaderboards: Leaderboards? = null
  private var achievements: Achievements? = null
  private var player: Player? = null
  private var saveGame: SaveGame? = null
  private var auth = Auth()
  //endregion

  //region FlutterPlugin
  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    setupChannel(binding.binaryMessenger)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    teardownChannel()
  }

  private fun setupChannel(messenger: BinaryMessenger) {
    channel = MethodChannel(messenger, CHANNEL_NAME)
    channel?.setMethodCallHandler(this)
  }

  private fun teardownChannel() {
    channel?.setMethodCallHandler(null)
    channel = null
  }
  //endregion

  //region ActivityAware
  private fun disposeActivity() {
    activityPluginBinding?.removeActivityResultListener(auth)
    activityPluginBinding = null
    leaderboards = null
    achievements = null
    saveGame = null
    player = null
  }

  private fun init() {
    val activityPluginBinding = activityPluginBinding ?: return
    leaderboards = Leaderboards(activityPluginBinding)
    achievements = Achievements(activityPluginBinding)
    saveGame = SaveGame(activityPluginBinding)
    player = Player()
  }

  override fun onDetachedFromActivity() {
    disposeActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activityPluginBinding = binding
    activity = binding.activity
    binding.addActivityResultListener(auth)
    init()
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }
  //endregion

  //region MethodCallHandler
  override fun onMethodCall(call: MethodCall, result: Result) {
    val method = methodsFrom(call.method)
    if (method == null) {
      result.notImplemented()
      return
    }
    when (method) {
      Method.SilentSignIn -> {
        val shouldEnableSavedGame = call.argument<Boolean>("shouldEnableSavedGame") ?: false
        auth.silentSignIn(activity, shouldEnableSavedGame, result)
      }
      Method.IsSignedIn -> {
        auth.isSignedIn(activity, result)
      }
      Method.SignOut -> {
        auth.signOut(result)
      }
      Method.ShowAchievements -> {
        achievements?.showAchievements(activity, result)
      }
      Method.LoadAchievements -> {
        achievements?.loadAchievements(activity, result)
      }
      Method.Unlock -> {
        val achievementID = call.argument<String>("achievementID") ?: ""
        achievements?.unlock(achievementID, result)
      }
      Method.Increment -> {
        val achievementID = call.argument<String>("achievementID") ?: ""
        val steps = call.argument<Int>("steps") ?: 1
        achievements?.increment(achievementID, steps, result)
      }
      Method.ShowLeaderboards -> {
        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
        leaderboards?.showLeaderboards(activity, leaderboardID, result)
      }
      Method.LoadLeaderboardScores -> {
        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
        val span = call.argument<Int>("span") ?: 0
        val leaderboardCollection = call.argument<Int>("leaderboardCollection") ?: 0
        val maxResults = call.argument<Int>("maxResults") ?: 0
        leaderboards?.loadLeaderboardScores(activity, leaderboardID, span, leaderboardCollection, maxResults, result)
      }
      Method.SubmitScore -> {
        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
        val score = call.argument<Int>("value") ?: 0
        leaderboards?.submitScore(leaderboardID, score, result)
      }
      Method.GetPlayerScore -> {
        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
        leaderboards?.getPlayerScore(leaderboardID, result)
      }
      Method.GetPlayerID -> {
        player?.getPlayerID(activity, result)
      }
      Method.GetPlayerName -> {
        player?.getPlayerName(activity, result)
      }
      Method.SaveGame -> {
        val data = call.argument<String>("data") ?: ""
        val name = call.argument<String>("name") ?: ""
        saveGame?.saveGame(data, name, name, result)
      }
      Method.LoadGame -> {
        val name = call.argument<String>("name") ?: ""
        saveGame?.loadGame(name, result)
      }
      Method.GetSavedGames -> {
        saveGame?.getSavedGames(result)
      }
      Method.DeleteGame -> {
        val name = call.argument<String>("name") ?: ""
        saveGame?.deleteGame(name, result)
      }
    }
  }
  //endregion
}
