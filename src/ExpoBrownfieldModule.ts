import { NativeModule, requireOptionalNativeModule } from 'expo';

import { ExpoBrownfieldModuleEvents } from './ExpoBrownfieldModule.types';

declare class ExpoBrownfieldModule extends NativeModule<ExpoBrownfieldModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

const module = requireOptionalNativeModule<ExpoBrownfieldModule>(
  'ExpoBrownfieldModule',
);
if (!module) {
  throw new Error('ExpoBrownfieldModule not found!!!!');
}

export default module;