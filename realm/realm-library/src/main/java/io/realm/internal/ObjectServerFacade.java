/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.internal;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmException;

/**
 * Class acting as an mediator between the basic Realm APIs and the Object Server APIs.
 * This breaks the cyclic dependency between ObjectServer and Realm code.
 */
public class ObjectServerFacade {

    public interface RealmCacheAccessor {
        Realm createRealmOrGetFromCache(RealmConfiguration configuration, OsSharedRealm.VersionID versionID);
    }

    public interface RealmInstanceFactory {
        Realm createInstance(OsSharedRealm sharedRealm);
    }

    public static final int SYNC_CONFIG_OPTIONS = 17;

    private static final ObjectServerFacade nonSyncFacade = new ObjectServerFacade();
    private static ObjectServerFacade syncFacade = null;

    static {
        //noinspection TryWithIdenticalCatches
        try {
            @SuppressWarnings("LiteralClassName")
            Class syncFacadeClass = Class.forName("io.realm.internal.SyncObjectServerFacade");
            //noinspection unchecked
            syncFacade = (ObjectServerFacade) syncFacadeClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException ignored) {
        } catch (InstantiationException e) {
            throw new RealmException("Failed to init SyncObjectServerFacade", e);
        } catch (IllegalAccessException e) {
            throw new RealmException("Failed to init SyncObjectServerFacade", e);
        } catch (NoSuchMethodException e) {
            throw new RealmException("Failed to init SyncObjectServerFacade", e);
        } catch (InvocationTargetException e) {
            throw new RealmException("Failed to init SyncObjectServerFacade", e.getTargetException());
        }
    }

    /**
     * Initializes the Object Server library
     */
    public void initialize(Context context, String userAgent, RealmCacheAccessor accessor, RealmInstanceFactory instantiator) {
    }

    /**
     * The last instance of this Realm was closed (across all Threads).
     */
    public void realmClosed(RealmConfiguration configuration) {
    }

    public Object[] getSyncConfigurationOptions(RealmConfiguration config) {
        return new Object[SYNC_CONFIG_OPTIONS];
    }

    public static ObjectServerFacade getFacade(boolean needSyncFacade) {
        if (needSyncFacade) {
            return syncFacade;
        }
        return nonSyncFacade;
    }

    // Returns a SyncObjectServerFacade instance if the class exists. Otherwise returns a non-sync one.
    public static ObjectServerFacade getSyncFacadeIfPossible() {
        if (syncFacade != null) {
            return syncFacade;
        }
        return nonSyncFacade;
    }

    // If no session yet exists for this path. Wrap a new Java Session around an existing OS one.
    public void wrapObjectStoreSessionIfRequired(OsRealmConfig config) {
    }

    public String getSyncServerCertificateAssetName(RealmConfiguration config) {
        return null;
    }

    public String getSyncServerCertificateFilePath(RealmConfiguration config) {
        return null;
    }

    /**
     * Block until all latest changes have been downloaded from the server. This should only
     * be called the first time a Realm file is created.
     *
     * @throws {@code DownloadingRealmInterruptedException} if the thread was interrupted while blocked waiting for
     * this to complete.
     * @throws {@code ObjectServerException } In any other kind of error is reported.
     */
    @SuppressWarnings("JavaDoc")
    public void downloadInitialRemoteChanges(RealmConfiguration config) {
        // Do nothing
    }

    /**
     * Check if an exception is a {@code DownloadingRealmInterruptedException}
     */
    public boolean wasDownloadInterrupted(Throwable throwable) {
        return false;
    }

    public void createNativeSyncSession(RealmConfiguration configuration) {
        // Do nothing
    }

    public void checkFlexibleSyncEnabled(RealmConfiguration configuration) {
        // Do nothing
    }

    public void downloadInitialFlexibleSyncData(Realm realm, RealmConfiguration configuration) {
        // Do nothing
    }
}
