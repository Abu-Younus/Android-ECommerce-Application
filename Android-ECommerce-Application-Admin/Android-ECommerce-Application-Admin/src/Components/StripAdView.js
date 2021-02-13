import React from "react";
import { Box, IconButton, Menu, MenuItem } from "@material-ui/core";
import { MoreVert } from "@material-ui/icons";

const StripAdView = (props) => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  return (
    <Box>
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
      <img style={{ width: "100%", height: "100px", 
      background:props.background, objectFit:"scale-down" }} src={props.image} />
    </Box>
  );
};

export default StripAdView;
