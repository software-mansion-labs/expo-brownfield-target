import { requireNativeView } from 'expo';
import * as React from 'react';

import { ExpoBrownfieldModuleViewProps } from './ExpoBrownfieldModule.types';

const NativeView: React.ComponentType<ExpoBrownfieldModuleViewProps> =
  requireNativeView('ExpoBrownfieldModule');

export default function ExpoBrownfieldModuleView(
  props: ExpoBrownfieldModuleViewProps,
) {
  return <NativeView {...props} />;
}
