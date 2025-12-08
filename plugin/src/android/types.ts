export type PublicationType =
  | 'localMaven'
  | 'localDirectory'
  | 'remotePublic'
  | 'remotePrivate'
  | 'remotePrivateToken';

export interface LocalMavenPublication {
  type: 'localMaven';
}

export interface LocalDirectoryPublication {
  type: 'localDirectory';
  name?: string;
  path: string;
}

export interface RemotePublicPublication {
  type: 'remotePublic';
  name?: string;
  url: string;
}

export interface RemotePrivateBasicPublication {
  type: 'remotePrivate';
  name?: string;
  url: string;
  username: string;
  password: string;
}

export type Publication =
  | LocalMavenPublication
  | LocalDirectoryPublication
  | RemotePublicPublication
  | RemotePrivateBasicPublication;

export interface PluginConfig {
  libraryName: string;
  package: string;
  packagePath: string;
  projectRoot: string;
  publishing: Publication[];
}

export type AndroidPluginProps = Pick<
  PluginConfig,
  'libraryName' | 'package' | 'publishing'
>;

export type PluginProps = Partial<AndroidPluginProps> | undefined;
