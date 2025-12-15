import { SFSymbols6_0 } from 'sf-symbols-typescript';

interface HeaderColors {
  light: string;
  dark: string;
}

interface HeaderIcon {
  color: string;
  name: SFSymbols6_0;
}

type HeaderImage = 'reactLogo' | HeaderIcon;

export interface HeaderOptions {
  headerBackgroundColor: HeaderColors;
  headerImage: HeaderImage;
  showWave?: boolean;
  title: string;
}

export interface HeaderImageProps {
  image: HeaderImage;
}

export interface RootLayoutProps {
  children: React.ReactNode;
  headerOptions: HeaderOptions;
}
