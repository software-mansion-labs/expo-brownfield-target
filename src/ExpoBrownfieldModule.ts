import { NativeModule, requireOptionalNativeModule } from 'expo';
import { ExpoBrownfieldModuleEvents } from './types';

declare class ExpoBrownfieldModuleSpec extends NativeModule<ExpoBrownfieldModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

const module = requireOptionalNativeModule<ExpoBrownfieldModuleSpec>(
  'ExpoBrownfieldModule',
);
if (!module) {
  const errorMessage = `Native module: ExpoBrownfieldModule not available
  Please ensure that your project is configured according to the documentation: https://github.com/software-mansion-labs/expo-brownfield-target/blob/main/README.md`;
  throw new Error(errorMessage);
}

export default module;
