/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.realm;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static net.iGap.Config.REALM_LATEST_MIGRATION_VERSION;

public class RealmMigration implements io.realm.RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("keepRoom", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessageSchema = schema.get("RealmRoomMessage");
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("authorRoomId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("actionStateUserId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelRoomSchema = schema.get("RealmChannelRoom");
            if (realmChannelRoomSchema != null) {
                realmChannelRoomSchema.addField("seenId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 3) {
            schema.create(RealmWallpaper.class.getSimpleName()).addField(RealmWallpaperFields.LAST_TIME_GET_LIST, long.class, FieldAttribute.REQUIRED).addField("wallPaperList", byte[].class).addField(RealmWallpaperFields.LOCAL_LIST, byte[].class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            schema.create(RealmPrivacy.class.getSimpleName()).addField("whoCanSeeMyAvatar", String.class).addField("whoCanInviteMeToChannel", String.class).addField("whoCanInviteMeToGroup", String.class).addField("whoCanSeeMyLastSeen", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.SHOW_TIME, boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.HAS_EMOJI_IN_TEXT, boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.LINK_INFO, String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 6) {
            RealmObjectSchema realmRoomSchema = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoomSchema != null) {
                realmRoomSchema.addField(RealmRoomFields.LAST_SCROLL_POSITION_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 7) {
            RealmObjectSchema realmPhoneContacts = schema.create(RealmPhoneContacts.class.getSimpleName()).addField(RealmPhoneContactsFields.PHONE, String.class).addField(RealmPhoneContactsFields.FIRST_NAME, String.class).addField(RealmPhoneContactsFields.LAST_NAME, String.class);
            realmPhoneContacts.addPrimaryKey(RealmPhoneContactsFields.PHONE);
            oldVersion++;
        }

        if (oldVersion == 8) {
            RealmObjectSchema roomSchema = schema.get(RealmRoom.class.getSimpleName());
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (roomSchema != null) {
                roomSchema.addRealmObjectField("firstUnreadMessage", realmRoomMessageSchema);
            }

            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("futureMessageId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 9) {
            schema.create(RealmCallConfig.class.getSimpleName()).addField(RealmCallConfigFields.VOICE_CALLING, boolean.class, FieldAttribute.REQUIRED).addField(RealmCallConfigFields.VIDEO_CALLING, boolean.class, FieldAttribute.REQUIRED).addField(RealmCallConfigFields.SCREEN_SHARING, boolean.class, FieldAttribute.REQUIRED).addField("IceServer", byte[].class);

            RealmObjectSchema realmCallLog = schema.create(RealmCallLog.class.getSimpleName()).addField(RealmCallLogFields.ID, long.class, FieldAttribute.REQUIRED).addField(RealmCallLogFields.NAME, String.class).addField(RealmCallLogFields.TIME, long.class, FieldAttribute.REQUIRED).addField(RealmCallLogFields.LOG_PROTO, byte[].class);
            realmCallLog.addPrimaryKey(RealmCallLogFields.ID);
            oldVersion++;
        }

        if (oldVersion == 10) {
            RealmObjectSchema realmPrivacySchema = schema.get(RealmPrivacy.class.getSimpleName());
            if (realmPrivacySchema != null) {
                realmPrivacySchema.addField(RealmPrivacyFields.WHO_CAN_VOICE_CALL_TO_ME, String.class);
            }

            RealmObjectSchema realmGroupSchema = schema.get(RealmGroupRoom.class.getSimpleName());
            if (realmGroupSchema != null) {
                realmGroupSchema.addField("participants_count", int.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelSchema = schema.get(RealmChannelRoom.class.getSimpleName());
            if (realmChannelSchema != null) {
                realmChannelSchema.addField("participants_count", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 11) {
            RealmObjectSchema realmClientCondition = schema.get(RealmClientCondition.class.getSimpleName());

            RealmObjectSchema realmOfflineListen = schema.create(RealmOfflineListen.class.getSimpleName()).addField(RealmOfflineListenFields.ID, long.class, FieldAttribute.REQUIRED).addField("offlineListen", long.class);
            realmOfflineListen.addPrimaryKey(RealmOfflineListenFields.ID);

            if (realmClientCondition != null) {
                realmClientCondition.addRealmListField("offlineListen", realmOfflineListen);
            }

            RealmObjectSchema realmAvatar = schema.get(RealmAvatar.class.getSimpleName());
            if (realmAvatar != null) {
                realmAvatar.addIndex(RealmAvatarFields.OWNER_ID);
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField(RealmRoomFields.IS_PINNED, boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 12) {
            RealmObjectSchema realmGeoNearbyDistance = schema.create(RealmGeoNearbyDistance.class.getSimpleName()).addField(RealmGeoNearbyDistanceFields.USER_ID, long.class).addField(RealmGeoNearbyDistanceFields.HAS_COMMENT, boolean.class).addField(RealmGeoNearbyDistanceFields.DISTANCE, int.class).addField(RealmGeoNearbyDistanceFields.COMMENT, String.class);
            realmGeoNearbyDistance.addPrimaryKey(RealmGeoNearbyDistanceFields.USER_ID);

            schema.create(RealmGeoGetConfiguration.class.getSimpleName()).addField(RealmGeoGetConfigurationFields.MAP_CACHE, String.class);
            oldVersion++;
        }

        if (oldVersion == 13) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField(RealmUserInfoFields.IS_PASS_CODE, boolean.class, FieldAttribute.REQUIRED).addField(RealmUserInfoFields.IS_FINGER_PRINT, boolean.class, FieldAttribute.REQUIRED).addField(RealmUserInfoFields.KIND_PASS_CODE, int.class, FieldAttribute.REQUIRED).addField(RealmUserInfoFields.PASS_CODE, String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 14) {
            RealmObjectSchema realmOfflineDelete = schema.get(RealmOfflineDelete.class.getSimpleName());
            if (realmOfflineDelete != null) {
                realmOfflineDelete.addField(RealmOfflineDeleteFields.BOTH, boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField(RealmRoomFields.PIN_ID, long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema registeredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (registeredInfo != null) {
                registeredInfo.addField("bio", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 15) {
            RealmObjectSchema realmChannelRoom = schema.get(RealmChannelRoom.class.getSimpleName());
            if (realmChannelRoom != null) {
                realmChannelRoom.addField("verified", boolean.class, FieldAttribute.REQUIRED);
                realmChannelRoom.addField("reactionStatus", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 16) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("importContactLimit", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmAttachment = schema.get(RealmAttachment.class.getSimpleName());
            if (realmAttachment != null) {
                realmAttachment.addField("url", String.class);
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("lastScrollPositionOffset", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 17) {
            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField(RealmRoomFields.PIN_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
                realmRoom.addField(RealmRoomFields.PIN_MESSAGE_ID_DELETED, long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessage = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessage != null) {
                if (realmRoomMessage.hasField("log")) {
                    realmRoomMessage.removeField("log");
                }

                if (realmRoomMessage.hasField("logMessage")) {
                    realmRoomMessage.removeField("logMessage");
                }

                realmRoomMessage.addField(RealmRoomMessageFields.LOGS, byte[].class);
            }

            RealmObjectSchema realmRegisteredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (realmRegisteredInfo != null) {
                realmRegisteredInfo.addField("verified", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmContact = schema.get(RealmContacts.class.getSimpleName());
            if (realmContact != null) {
                realmContact.addField("verified", boolean.class, FieldAttribute.REQUIRED);
                realmContact.addField("mutual", boolean.class, FieldAttribute.REQUIRED);
                realmContact.addField("bio", String.class);
            }

            if (schema.contains("RealmRoomMessageLog")) {
                schema.remove("RealmRoomMessageLog");
            }

            oldVersion++;
        }

        if (oldVersion == 18) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField(RealmUserInfoFields.PUSH_NOTIFICATION_TOKEN, String.class);
            }

            oldVersion++;
        }

        if (oldVersion == 19) {

            RealmObjectSchema realmRoomMessageWallet = schema.create(RealmRoomMessageWallet.class.getSimpleName())
                    .addField(RealmRoomMessageWalletFields.ID, long.class, FieldAttribute.REQUIRED)
                    .addPrimaryKey(RealmRoomMessageWalletFields.ID)
                    .addField(RealmRoomMessageWalletFields.FROM_USER_ID, long.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.TO_USER_ID, long.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.AMOUNT, long.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.TRACE_NUMBER, long.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.INVOICE_NUMBER, long.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.PAY_TIME, int.class, FieldAttribute.REQUIRED)
                    .addField(RealmRoomMessageWalletFields.TYPE, String.class)
                    .addField(RealmRoomMessageWalletFields.DESCRIPTION, String.class);

            RealmObjectSchema realmRoomMessage = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessage != null) {
                realmRoomMessage.addRealmObjectField("roomMessageWallet", realmRoomMessageWallet);
            }

            oldVersion++;
        }

        if (oldVersion == 20) {

            RealmObjectSchema realmIceServer = schema.create(RealmIceServer.class.getSimpleName()).addField("url", String.class).addField("username", String.class).addField("credential", String.class);

            RealmObjectSchema realmWallpaperProto = schema.create(RealmWallpaperProto.class.getSimpleName()).addRealmObjectField("file", schema.get(RealmAttachment.class.getSimpleName())).addField("color", String.class);

            RealmObjectSchema realmCallConfig = schema.get(RealmCallConfig.class.getSimpleName());
            if (realmCallConfig != null) {
                realmCallConfig.addRealmListField("realmIceServer", realmIceServer);

                if (realmCallConfig.hasField("IceServer")) {
                    realmCallConfig.removeField("IceServer");
                }
            }

            RealmObjectSchema realmWallpaper = schema.get(RealmWallpaper.class.getSimpleName());
            if (realmWallpaper != null) {
                realmWallpaper.addRealmListField("realmWallpaperProto", realmWallpaperProto);

                if (realmWallpaper.hasField("wallPaperList")) {
                    realmWallpaper.removeField("wallPaperList");
                }
            }

            oldVersion++;
        }

        if (oldVersion == 21) {

            schema.create(RealmDataUsage.class.getSimpleName()).addField("type", String.class)
                    .addField("downloadSize", long.class, FieldAttribute.REQUIRED)
                    .addField("uploadSize", long.class, FieldAttribute.REQUIRED)
                    .addField("connectivityType", boolean.class, FieldAttribute.REQUIRED)
                    .addField("numUploadedFiles", int.class, FieldAttribute.REQUIRED)
                    .addField("numDownloadedFile", int.class, FieldAttribute.REQUIRED);

            oldVersion++;
        }

        if (oldVersion == 22) {

            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("isPattern", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 23) {

            RealmObjectSchema realmRegisteredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (realmRegisteredInfo != null) {
                realmRegisteredInfo.addField("isBot", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 24) {

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("priority", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == REALM_LATEST_MIGRATION_VERSION) { // REALM_LATEST_MIGRATION_VERSION = 25

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("isFromPromote", boolean.class, FieldAttribute.REQUIRED);
                realmRoom.addField("promoteId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmPrivacy = schema.get(RealmPrivacy.class.getSimpleName());
            if (realmPrivacy != null) {
                realmPrivacy.addField("whoCanVideoCallToMe", String.class);
            }

            oldVersion++;
        }
    }
}
