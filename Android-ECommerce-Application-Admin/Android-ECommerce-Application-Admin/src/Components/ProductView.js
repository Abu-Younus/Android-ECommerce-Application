import { Box, Typography } from '@material-ui/core'
import { green, grey } from '@material-ui/core/colors'
import React from 'react'

const ProductView = (props) => {
    return (
        <Box padding="18px" bgcolor="white" boxShadow="8px" mx="4px" borderRadius="16px">
            <img src={props.item.image}
            style={{width:"120px", height:"120px", objectFit:"scale-down" , backgroundColor:grey[50]}} />
            <Box width="110px" component="div" textOverflow="ellipsis">
                <Typography variant="subtitle1">{props.item.title}</Typography>
            </Box>
            <Typography variant="subtitle2">
                <span style={{color:green.A700}}>{props.item.subtitle}</span>
            </Typography>
            <Typography variant="h6">BDT. {props.item.price}</Typography>
        </Box>
    )
}

export default ProductView
