
/* eslint-disable*/ 
import ajax from './ajax'

// set coordinate
export const setCoordinate = (data) => ajax('/set/', data) 
// undo
export const undo = () => ajax('/undo')
// get type
export const getType = (i) => ajax('/type/', i)
// get history
export const getAllSteps = () => ajax('/getAllStep')
// reset
export const reset = () => ajax('/reset')

// get instance array
export const findInstance = (instance) => ajax('/getInstance/', instance)
