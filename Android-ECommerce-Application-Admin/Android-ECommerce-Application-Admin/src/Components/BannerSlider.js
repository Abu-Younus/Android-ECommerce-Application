import React from 'react';
import { useTheme } from '@material-ui/core/styles';
import SwipeableViews from 'react-swipeable-views';
import { autoPlay } from 'react-swipeable-views-utils';
import { blueGrey } from '@material-ui/core/colors';
import { IconButton, Menu, MenuItem } from '@material-ui/core';
import { MoreVert } from '@material-ui/icons';

const AutoPlaySwipeableViews = autoPlay(SwipeableViews);

const BannerSlider = (props) => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
    const theme = useTheme();
    const [activeStep, setActiveStep] = React.useState(0);

    const handleStepChange = (step) => {
        setActiveStep(step);
      };
    
      return (
        <div>
          <div style={{backgroundColor:"white",textAlign:"right"}}>
            <IconButton
              aria-label="more"
              aria-controls="long-menu"
              aria-haspopup="true"
              onClick={handleClick}
            >
            <MoreVert />
            </IconButton>
            <Menu
              id="long-menu"
              anchorEl={anchorEl}
              keepMounted
              open={open}
              onClose={handleClose}
              PaperProps={{
              style: {
                width: '20ch',
              },
              }}
            >
              <MenuItem onClick={()=>{
                  props.edit()
                  handleClose()
                }}>
                Edit
              </MenuItem>
              <MenuItem onClick={()=>{
                  props.delete()
                  handleClose()
                }}>
                Delete
              </MenuItem>
            </Menu>
          </div>
          <AutoPlaySwipeableViews
            axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
            index={activeStep}
            onChangeIndex={handleStepChange}
            enableMouseEvents
          >
            {props.Images.map((step, index) => (
              <div key={index} style={{width:"100%"}}>
                {Math.abs(activeStep - index) <= 2 ? (
                  <img 
                  style={{width:"100%",height:"250px",
                        objectFit:"scale-down",
                        backgroundColor:step.background}} src={step.banner} alt="" />
                ) : null}
              </div>
            ))}
          </AutoPlaySwipeableViews>
        </div>
      );
}

export default BannerSlider