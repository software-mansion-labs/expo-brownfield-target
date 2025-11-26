import { registerWebModule, NativeModule } from 'expo';

import { ChangeEventPayload } from './ExpoBrownfieldModule.types';

type ExpoBrownfieldModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
};

class ExpoBrownfieldModule extends NativeModule<ExpoBrownfieldModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(ExpoBrownfieldModule, 'ExpoBrownfieldModule');
