export type PublicationType =
  | 'localMaven'
  | 'localDirectory'
  | 'remotePublic'
  | 'remotePrivateBasic'
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
  type: 'remotePrivateBasic';
  name?: string;
  url: string;
  username: string;
  password: string;
}

export interface RemotePrivateTokenPublication {
  type: 'remotePrivateToken';
  name?: string;
  url: string;
  token: string;
}
export type Publication =
  | LocalMavenPublication
  | LocalDirectoryPublication
  | RemotePublicPublication
  | RemotePrivateBasicPublication
  | RemotePrivateTokenPublication;

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
