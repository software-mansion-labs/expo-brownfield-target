import * as React from 'react';

import { ExpoBrownfieldModuleViewProps } from './ExpoBrownfieldModule.types';

export default function ExpoBrownfieldModuleView(
  props: ExpoBrownfieldModuleViewProps,
) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
